package analysis.visitor;

import analysis.model.AnalysisError;
import analysis.model.ConditionStates;
import analysis.model.ExpressionAnalysisState;
import analysis.model.VariablesState;
import analysis.values.*;
import analysis.values.visitor.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.modules.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedParameterDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserFieldDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserMethodDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserParameterDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserVariableDeclaration;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import utils.*;

import java.util.*;

public class ExpressionVisitor implements GenericVisitor<PossibleValues, ExpressionAnalysisState> {
    private final ConditionVisitor conditionVisitor;
    private final MergeVisitor mergeVisitor;
    private final AddVisitor addVisitor;
    private final DivideVisitor divideVisitor;
    private final SubtractVisitor subtractVisitor;
    private final MultiplyVisitor multiplyVisitor;
    private RestrictGreaterThanVisitor restrictGTVisitor;
    private RestrictGreaterThanOrEqualVisitor restrictGTEVisitor;
    private RestrictLessThanVisitor restrictLTVisitor;
    private RestrictLessThanOrEqualVisitor restrictLTEVisitor;

    public ExpressionVisitor() {
        this(new MergeVisitor(), new AddVisitor(), new DivideVisitor(), new MultiplyVisitor(), new SubtractVisitor());
    }

    public ExpressionVisitor(
            MergeVisitor mergeVisitor,
            AddVisitor addVisitor,
            DivideVisitor divideVisitor,
            MultiplyVisitor multiplyVisitor,
            SubtractVisitor subtractVisitor
    ) {
        this.mergeVisitor = mergeVisitor;
        this.addVisitor = addVisitor;
        this.divideVisitor = divideVisitor;
        this.multiplyVisitor = multiplyVisitor;
        this.subtractVisitor = subtractVisitor;
        this.restrictGTVisitor = new RestrictGreaterThanVisitor();
        this.restrictGTEVisitor = new RestrictGreaterThanOrEqualVisitor();
        this.restrictLTVisitor = new RestrictLessThanVisitor();
        this.restrictLTEVisitor = new RestrictLessThanOrEqualVisitor();
        this.conditionVisitor = new ConditionVisitor(this, mergeVisitor, new IntersectVisitor());
    }

    @Override
    public PossibleValues visit(VariableDeclarationExpr n, ExpressionAnalysisState arg) {
        for (VariableDeclarator declarator : n.getVariables()) {
            declarator.accept(this, arg);
        }
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(VariableDeclarator n, ExpressionAnalysisState arg) {
        if (n.getInitializer().isPresent()) {
            PossibleValues value = n.getInitializer().get().accept(this, arg);
            ResolvedType type = ResolverUtil.resolveOrNull(n.getType());
            if (type != null) value = VariableUtil.implicitTypeCasting(type, n.getInitializer().get(), value, arg);
            VariablesState state = arg.getVariablesState();
            state.setVariable(n, value);
            return value;
        }
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(AssignExpr n, ExpressionAnalysisState arg) {
        PossibleValues left = n.getTarget().accept(this, arg);
        PossibleValues right = n.getValue().accept(this, arg);
        AssignExpr.Operator operator = n.getOperator();
        PossibleValues result;
        switch (operator) {
            case DIVIDE -> result = handleDivide(n, n.getTarget(), n.getValue(), left, right, arg);
            case PLUS -> result = handlePlus(n, n.getTarget(), n.getValue(), left, right, arg);
            case MINUS -> result = handleMinus(n, n.getTarget(), n.getValue(), left, right, arg);
            case MULTIPLY -> result = handleMultiply(n, n.getTarget(), n.getValue(), left, right, arg);
            default -> result = right;
        }
        ResolvedType type = ResolverUtil.calculateResolvedTypeOrNull(n.getTarget());
        if (type != null) result = VariableUtil.implicitTypeCasting(type, n.getValue(), result, arg);
        VariableUtil.setVariableFromExpression(n.getTarget(), result, arg.getVariablesState());
        return result;
    }

    @Override
    public PossibleValues visit(ArrayAccessExpr n, ExpressionAnalysisState arg) {
        PossibleValues indexValue = n.getIndex().accept(this, arg);
        PossibleValues arrayNameValue = n.getName().accept(this, arg);

        // Check null
        if (arrayNameValue.canBeNull()) {
            arg.addError(new AnalysisError(NullPointerException.class, n.getName(), arrayNameValue == NullValue.VALUE));
        }

        // Check valid index
        IntegerValue length;
        if (arrayNameValue instanceof ArrayValue arrayValue) {
            length = arrayValue.getLength();
        } else {
            length = ArrayValue.DEFAULT_LENGTH;
        }

        PossibleValues validIndex = indexValue.acceptAbstractOp(restrictGTEVisitor, new IntegerRange(0));
        validIndex = validIndex.acceptAbstractOp(restrictLTVisitor, length);
        PossibleValues validLength = length.acceptAbstractOp(restrictGTVisitor, validIndex);

        PossibleValues lessThanZeroIndex = indexValue.acceptAbstractOp(restrictLTVisitor, new IntegerRange(0));
        PossibleValues greaterThanLengthIndex = indexValue.acceptAbstractOp(restrictGTEVisitor, length);
        if (!lessThanZeroIndex.isEmpty() || !greaterThanLengthIndex.isEmpty()) {
            arg.addError(new AnalysisError(ArrayIndexOutOfBoundsException.class, n, validIndex.isEmpty()));
        }

        if (n.getName().isNameExpr()) {
            // Update array length
            VariableUtil.updateArrayLength(n.getName().asNameExpr().resolve(), validLength, arg.getVariablesState());
        }
        // Update index variable
        VariableUtil.setVariableFromExpression(n.getIndex(), validIndex, arg.getVariablesState());

        // Return proper value
        ResolvedType type = n.getName().calculateResolvedType();
        if (type.isArray()) {
            ResolvedType componentType = type.asArrayType().getComponentType();
            return ValueUtil.getValueForType(componentType);
        }
        return AnyValue.VALUE;
    }

    @Override
    public PossibleValues visit(ArrayCreationExpr n, ExpressionAnalysisState arg) {
        ArrayCreationLevel arrayCreationLevel = n.getLevels().getFirst().orElse(null); // only 1D array
        if (arrayCreationLevel != null && arrayCreationLevel.getDimension().isPresent()) {
            Expression e = arrayCreationLevel.getDimension().get();
            PossibleValues dimensionValue = e.accept(this, arg);
            if (dimensionValue instanceof IntegerValue intDimensionValue) {
                PossibleValues validSize = intDimensionValue.acceptAbstractOp(restrictGTEVisitor, new IntegerRange(0));
                if (!Objects.equals(validSize, intDimensionValue)) {
                    arg.addError(new AnalysisError(NegativeArraySizeException.class, n, validSize.isEmpty()));
                }
                // Update possible values of size variable
                VariableUtil.setVariableFromExpression(e, validSize, arg.getVariablesState());
                return ArrayValue.create(validSize, false);
            }
        }
        if (n.getInitializer().isPresent()) {
            return n.getInitializer().get().accept(this, arg);
        }
        return new ArrayValue();
    }

    @Override
    public PossibleValues visit(ArrayInitializerExpr n, ExpressionAnalysisState arg) {
        // TODO: handle array initializer
        return new ArrayValue();
    }

    @Override
    public PossibleValues visit(BinaryExpr n, ExpressionAnalysisState arg) {
        switch (n.getOperator()) {
            case AND, OR, GREATER, GREATER_EQUALS, LESS, LESS_EQUALS, EQUALS, NOT_EQUALS -> {
                ConditionStates condStates = n.accept(conditionVisitor, arg);
                arg.getVariablesState().copyValuesFrom(condStates.getTrueState());
                arg.getVariablesState().merge(mergeVisitor, condStates.getFalseState());
                if (!condStates.getTrueState().isDomainEmpty() &&
                        !condStates.getFalseState().isDomainEmpty()) return BooleanValue.ANY_VALUE;
                if (!condStates.getTrueState().isDomainEmpty()) return BooleanValue.TRUE;
                if (!condStates.getFalseState().isDomainEmpty()) return BooleanValue.FALSE;
                return EmptyValue.VALUE;
            }
        }
        PossibleValues leftValue = n.getLeft().accept(this, arg);
        PossibleValues rightValue = n.getRight().accept(this, arg);
        BinaryExpr.Operator operator = n.getOperator();
        PossibleValues result;
        switch (operator) {
            case DIVIDE -> result = handleDivide(n, n.getLeft(), n.getRight(), leftValue, rightValue, arg);
            case PLUS -> result = handlePlus(n, n.getLeft(), n.getRight(), leftValue, rightValue, arg);
            case MINUS -> result = handleMinus(n, n.getLeft(), n.getRight(), leftValue, rightValue, arg);
            case MULTIPLY -> result = handleMultiply(n, n.getLeft(), n.getRight(), leftValue, rightValue, arg);
            default -> result = AnyValue.VALUE;
        }
        return result;
    }

    /**
     * Handle plus operation
     */
    private PossibleValues handlePlus(
            Expression expr,
            Expression leftExpr,
            Expression rightExpr,
            PossibleValues leftValue,
            PossibleValues rightValue,
            ExpressionAnalysisState arg
    ) {
        ResolvedType type = ResolverUtil.calculateResolvedTypeOrNull(expr);
        if (type != null) {
            if (TypeUtil.isStringType(type)) {
                return ValueUtil.stringValueConcat(leftValue, rightValue);
            }
            boolean isDefinite = handleNullErrors(expr, leftExpr, rightExpr, leftValue, rightValue, arg);
            if (isDefinite) return EmptyValue.VALUE;
            return leftValue.acceptAbstractOp(addVisitor, rightValue);
        }
        return AnyValue.VALUE;
    }

    /**
     * Handle divide operation
     */
    private PossibleValues handleDivide(
            Expression expr,
            Expression leftExpr,
            Expression rightExpr,
            PossibleValues leftValue,
            PossibleValues rightValue,
            ExpressionAnalysisState arg
    ) {
        boolean isDefinite = handleNullErrors(expr, leftExpr, rightExpr, leftValue, rightValue, arg);
        if (!isDefinite) {
            PairValue<PossibleValues, AnalysisError> quotient = leftValue.acceptAbstractOp(divideVisitor, rightValue);
            AnalysisError error = quotient.getB();
            if (error != null) {
                arg.addError(error.atNode(expr));
            }
            return quotient.getA();
        }
        return EmptyValue.VALUE;
    }

    /**
     * Handle minus operation
     */
    private PossibleValues handleMinus(
            Expression expr,
            Expression leftExpr,
            Expression rightExpr,
            PossibleValues leftValue,
            PossibleValues rightValue,
            ExpressionAnalysisState arg
    ) {
        boolean isDefinite = handleNullErrors(expr, leftExpr, rightExpr, leftValue, rightValue, arg);
        return isDefinite ? EmptyValue.VALUE : leftValue.acceptAbstractOp(subtractVisitor, rightValue);
    }

    /**
     * Handle multiply operation
     */
    private PossibleValues handleMultiply(
            Expression expr,
            Expression leftExpr,
            Expression rightExpr,
            PossibleValues leftValue,
            PossibleValues rightValue,
            ExpressionAnalysisState arg
    ) {
        boolean isDefinite = handleNullErrors(expr, leftExpr, rightExpr, leftValue, rightValue, arg);
        return isDefinite ? EmptyValue.VALUE : leftValue.acceptAbstractOp(multiplyVisitor, rightValue);
    }

    /**
     * Check NullPointerException in binary operation and properly restrict domain of values
     */
    private boolean handleNullErrors(
            Expression expr,
            Expression leftExpr,
            Expression rightExpr,
            PossibleValues leftValue,
            PossibleValues rightValue,
            ExpressionAnalysisState arg
    ) {
        if (leftValue.canBeNull() || rightValue.canBeNull()) {
            boolean isDefinite = leftValue == NullValue.VALUE || rightValue == NullValue.VALUE;
            arg.addError(new AnalysisError(NullPointerException.class, expr, isDefinite));
            if (leftValue instanceof ObjectValue objectValue) {
                VariableUtil.setVariableFromExpression(leftExpr, objectValue.withNotNullable(), arg.getVariablesState());
            }
            if (rightValue instanceof ObjectValue objectValue) {
                VariableUtil.setVariableFromExpression(rightExpr, objectValue.withNotNullable(), arg.getVariablesState());
            }
            return isDefinite;
        }
        return false;
    }

    @Override
    public PossibleValues visit(CastExpr n, ExpressionAnalysisState arg) {
        ResolvedType castType = n.getType().resolve();
        ResolvedType exprType = n.getExpression().calculateResolvedType();

        // check cast both ways
        if (!(exprType.isAssignableBy(castType) || castType.isAssignableBy(exprType))) {
            arg.addError(new AnalysisError(ClassCastException.class, n, true));
            return new EmptyValue();
        }

        return n.getExpression().accept(this, arg);
    }

    @Override
    public PossibleValues visit(ClassExpr n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ConditionalExpr n, ExpressionAnalysisState arg) {
        ConditionStates states = n.getCondition().accept(conditionVisitor, arg);
        VariablesState trueVarState = states.getTrueState();
        VariablesState falseVarState = states.getFalseState();
        ExpressionAnalysisState trueState = new ExpressionAnalysisState(trueVarState);
        ExpressionAnalysisState falseState = new ExpressionAnalysisState(falseVarState);
        PossibleValues trueValue = n.getThenExpr().accept(this, trueState);
        PossibleValues falseValue = n.getElseExpr().accept(this, falseState);

        VariablesState mergedState = new VariablesState();
        mergedState.setDomainEmpty();
        if (!trueVarState.isDomainEmpty()) mergedState.copyValuesFrom(trueVarState);
        if (!falseVarState.isDomainEmpty()) mergedState.merge(mergeVisitor, falseVarState);
        arg.getVariablesState().copyValuesFrom(mergedState);
        if (!trueVarState.isDomainEmpty() && !falseVarState.isDomainEmpty()) {
            return trueValue.acceptAbstractOp(mergeVisitor, falseValue);
        } else if (!trueVarState.isDomainEmpty()) {
            return trueValue;
        } else if (!falseVarState.isDomainEmpty()) {
            return falseValue;
        }
        return EmptyValue.VALUE;
    }

    @Override
    public PossibleValues visit(EnclosedExpr n, ExpressionAnalysisState arg) {
        return n.getInner().accept(this, arg);
    }

    @Override
    public PossibleValues visit(FieldAccessExpr n, ExpressionAnalysisState arg) {
        PossibleValues scopeValue = n.getScope().accept(this, arg);
        ResolvedValueDeclaration valDec = n.resolve();
        if (scopeValue.canBeNull()) {
            arg.addError(new AnalysisError(NullPointerException.class, n.getScope(), Objects.equals(scopeValue, NullValue.VALUE)));
            if (scopeValue instanceof ObjectValue objValue) {
                VariableUtil.setVariableFromExpression(n.getScope(), objValue.withNotNullable(), arg.getVariablesState());
            }
        }
        if (scopeValue instanceof ArrayValue arrayValue) {
            if (valDec.getName().equals("length")) {
                return arrayValue.getLength();
            }
        }
        if (scopeValue.isEmpty()) {
            return new EmptyValue();
        }
        // TODO: handle field access default value (+ annotations)
        return ValueUtil.getValueForType(valDec.getType());
    }

    @Override
    public PossibleValues visit(InstanceOfExpr n, ExpressionAnalysisState arg) {
        ResolvedType rightType = n.getType().resolve();
        ResolvedType leftType = n.getExpression().calculateResolvedType();
        if (rightType.isAssignableBy(leftType)) {
            return new BooleanValue(true, false);
        }
        if (leftType.isAssignableBy(rightType)) {
            return new BooleanValue(true, true);
        }
        return new BooleanValue(false, true);
    }

    @Override
    public PossibleValues visit(StringLiteralExpr n, ExpressionAnalysisState arg) {
        return new StringValue(n.asString());
    }

    @Override
    public PossibleValues visit(IntegerLiteralExpr n, ExpressionAnalysisState arg) {
        return new IntegerRange(n.asNumber().intValue(), n.asNumber().intValue());
    }

    @Override
    public PossibleValues visit(LongLiteralExpr n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(CharLiteralExpr n, ExpressionAnalysisState arg) {
        return new CharValue(n.asChar());
    }

    @Override
    public PossibleValues visit(DoubleLiteralExpr n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(BooleanLiteralExpr n, ExpressionAnalysisState arg) {
        return new BooleanValue(n.getValue());
    }

    @Override
    public PossibleValues visit(NullLiteralExpr n, ExpressionAnalysisState arg) {
        return NullValue.VALUE;
    }

    /**
     * A method call on an object in the form
     * a.b(c)
     * where a = method scope, b = method name, c = method arg(s)
     */
    @Override
    public PossibleValues visit(MethodCallExpr n, ExpressionAnalysisState arg) {
        Optional<Expression> scope = n.getScope();
        String methodName = n.getName().asString();

        // handle method scope if present
        if (scope.isPresent()) {
            Expression object = scope.get();
            PossibleValues scopeValue = object.accept(this, arg);

            if (scopeValue.isEmpty()) {
                return new EmptyValue();
            }
            if (scopeValue.canBeNull()) {
                arg.addError(new AnalysisError(NullPointerException.class, n, scopeValue == NullValue.VALUE));
                if (scopeValue instanceof ObjectValue objValue) {
                    VariableUtil.setVariableFromExpression(object, objValue.withNotNullable(), arg.getVariablesState());
                }
            }
            if (scopeValue instanceof StringValue stringValue) {
                if (methodName.equals("length")) {
                    return stringValue.getLengthApproximation();
                } else if (methodName.equals("isEmpty")) {
                    return stringValue.getIsEmptyApproximation();
                }
            }
        }

        ResolvedMethodDeclaration dec = n.resolve();

        // handle method args
        NodeList<Expression> methodArgs = n.getArguments();
        for (int i = 0; i < methodArgs.size(); i++) {
            Expression methodArg = methodArgs.get(i);
            PossibleValues expValue = methodArg.accept(this, arg);

            ResolvedParameterDeclaration paramDec = dec.getParam(i);
            if (paramDec instanceof JavaParserParameterDeclaration javaParamDec) {
                List<AnnotationExpr> annotations = javaParamDec.getWrappedNode().getAnnotations().stream().toList();
                List<AnalysisError> errors = AnnotationUtil.checkArgumentWithAnnotation(expValue, annotations, javaParamDec.getName(), n.toString());
                if (errors.size() != 0) {
                    arg.addErrors(errors);
                }
            }
        }

        // handle possible runtime exceptions
        if (dec instanceof JavaParserMethodDeclaration methodDec) {
            ResolvedReferenceTypeDeclaration runtimeExceptionType = new ReflectionTypeSolver().solveType("java.lang.RuntimeException");
            MethodDeclaration methodDeclaration = methodDec.getWrappedNode();

            // exceptions in signature
            List<ResolvedType> exceptionsInSignature = methodDeclaration.getThrownExceptions()
                    .stream()
                    .map(Type::resolve)
                    .filter(e -> runtimeExceptionType.isAssignableBy(e))
                    .toList();

            Set<ResolvedType> exceptions = new HashSet<>();
            exceptions.addAll(exceptionsInSignature);

            // exceptions in javadoc
            exceptions.addAll(JavadocUtil.getRuntimeThrows(methodDeclaration));

            for (ResolvedType rt: exceptions) {
                arg.addError(new AnalysisError(rt.describe(), n, false));
            }
            return ValueUtil.getValueForType(dec.getReturnType(), methodDeclaration.getAnnotations(), this);
        }

        return ValueUtil.getValueForType(dec.getReturnType());
    }

    @Override
    public PossibleValues visit(NameExpr n, ExpressionAnalysisState arg) {
        ResolvedValueDeclaration dec = ResolverUtil.resolveOrNull(n);
        if (dec == null) return AnyValue.VALUE;
        VariablesState state = arg.getVariablesState();
        if (dec instanceof JavaParserVariableDeclaration jpVarDec) {
            return state.getVariable(jpVarDec.getVariableDeclarator());
        }
        if (dec instanceof JavaParserParameterDeclaration jpParamDec) {
            return state.getVariable(jpParamDec.getWrappedNode());
        }
        if (dec instanceof JavaParserFieldDeclaration jpFieldDec) {
            return ValueUtil.getValueForType(dec.getType(), jpFieldDec.getWrappedNode().getAnnotations(), this);
        }
        return ValueUtil.getValueForType(dec.getType());
    }

    @Override
    public PossibleValues visit(ObjectCreationExpr n, ExpressionAnalysisState arg) {
        switch (n.getType().resolve().asReferenceType().getQualifiedName()) {
            case "java.lang.Integer":
            case "java.lang.Boolean":
            case "java.lang.Character":
                assert n.getArguments().size() == 1;
                return BoxedPrimitive.create(n.getArguments().get(0).accept(this, arg), false);
            default:
                return ExtendableObjectValue.VALUE;
        }
    }

    @Override
    public PossibleValues visit(ThisExpr n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(SuperExpr n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(UnaryExpr n, ExpressionAnalysisState arg) {
        PossibleValues preValue = n.getExpression().accept(this, arg);
        return unaryOperation(n.getExpression(), n.getOperator(), preValue, arg, false);
    }

    /**
     * Perform unary operations<br/>
     * Note: about wrappers<br/>
     * - '++'/'--' Integer will return Integer<br/>
     * - '+'/'-' Integer will return int<br/>
     * - '!' Boolean will return boolean<br/>
     */
    private PossibleValues unaryOperation(
            Expression expr,
            UnaryExpr.Operator operator,
            PossibleValues preValue,
            ExpressionAnalysisState arg,
            boolean asBoxed
    ) {
        if (preValue instanceof IntegerValue intValue) {
            PossibleValues postValue;
            switch (operator) {
                case PREFIX_INCREMENT, POSTFIX_INCREMENT -> {
                    postValue = intValue.acceptAbstractOp(addVisitor, new IntegerRange(1));
                    postValue = asBoxed ? BoxedPrimitive.create(postValue, false) : postValue;
                    preValue = asBoxed ? BoxedPrimitive.create(preValue, false) : preValue;
                    VariableUtil.setVariableFromExpression(expr, postValue, arg.getVariablesState());
                }
                case PREFIX_DECREMENT, POSTFIX_DECREMENT -> {
                    postValue = intValue.acceptAbstractOp(subtractVisitor, new IntegerRange(1));
                    postValue = asBoxed ? BoxedPrimitive.create(postValue, false) : postValue;
                    preValue = asBoxed ? BoxedPrimitive.create(preValue, false) : preValue;
                    VariableUtil.setVariableFromExpression(expr, postValue, arg.getVariablesState());
                }
                case MINUS -> {
                    postValue = new IntegerRange(
                            MathUtil.flipSignToLimit(intValue.getMax()),
                            MathUtil.flipSignToLimit(intValue.getMin())
                    );
                }
                case PLUS -> {
                    // If other types are added, this will specify any byte/short/char as int
                    postValue = intValue;
                }
                case BITWISE_COMPLEMENT -> {
                    postValue = IntegerRange.ANY_VALUE;
                }
                default -> {
                    postValue = preValue.isEmpty() ? new EmptyValue() : new AnyValue();
                }
            }
            return switch (operator) {
                case POSTFIX_INCREMENT, POSTFIX_DECREMENT -> preValue;
                default -> postValue;
            };
        } else if (preValue instanceof BooleanValue) {
            if (operator == UnaryExpr.Operator.LOGICAL_COMPLEMENT) {
                if (Objects.equals(preValue, BooleanValue.TRUE)) {
                    return BooleanValue.FALSE;
                } else if (Objects.equals(preValue, BooleanValue.FALSE)) {
                    return BooleanValue.TRUE;
                } else {
                    return BooleanValue.ANY_VALUE;
                }
            }
        } else if (preValue instanceof BoxedPrimitive boxedPrimitive) {
            if (boxedPrimitive.canBeNull()) {
                arg.addError(new AnalysisError(NullPointerException.class, expr, false));
            }
            return unaryOperation(expr, operator, boxedPrimitive.unbox(), arg, true);
        } else if (Objects.equals(preValue, NullValue.VALUE)) {
            arg.addError(new AnalysisError(NullPointerException.class, expr, true));
            arg.getVariablesState().setDomainEmpty();
            return new EmptyValue();
        }
        return preValue.isEmpty() ? new EmptyValue() : new AnyValue();
    }

    // region ----Not required----
    // Move any we're not using here
    @Override
    public PossibleValues visit(CompilationUnit n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(MethodDeclaration n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(BlockStmt n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ExpressionStmt n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(SwitchStmt n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(SwitchEntry n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(BreakStmt n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ReturnStmt n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(IfStmt n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(WhileStmt n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ContinueStmt n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(DoStmt n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ForEachStmt n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ForStmt n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ThrowStmt n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(TryStmt n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(CatchClause n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(Name n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(SimpleName n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(LabeledStmt n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(EmptyStmt n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(NodeList n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(PackageDeclaration n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(TypeParameter n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(LineComment n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(BlockComment n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ClassOrInterfaceDeclaration n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(RecordDeclaration n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(CompactConstructorDeclaration n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(EnumDeclaration n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(EnumConstantDeclaration n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(AnnotationDeclaration n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(AnnotationMemberDeclaration n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(FieldDeclaration n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ConstructorDeclaration n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(Parameter n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(InitializerDeclaration n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(JavadocComment n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ClassOrInterfaceType n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(PrimitiveType n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ArrayType n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ArrayCreationLevel n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(IntersectionType n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(MarkerAnnotationExpr n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(SingleMemberAnnotationExpr n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(NormalAnnotationExpr n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(MemberValuePair n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ExplicitConstructorInvocationStmt n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(LocalClassDeclarationStmt n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(LocalRecordDeclarationStmt n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(AssertStmt n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(UnionType n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(VoidType n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(WildcardType n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(UnknownType n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(SynchronizedStmt n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(LambdaExpr n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(MethodReferenceExpr n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(TypeExpr n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ImportDeclaration n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ModuleDeclaration n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ModuleRequiresDirective n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ModuleExportsDirective n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ModuleProvidesDirective n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ModuleUsesDirective n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ModuleOpensDirective n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(UnparsableStmt n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ReceiverParameter n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(VarType n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(Modifier n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(SwitchExpr n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(YieldStmt n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(TextBlockLiteralExpr n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(PatternExpr n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }
    // endregion ----Not required----
}
