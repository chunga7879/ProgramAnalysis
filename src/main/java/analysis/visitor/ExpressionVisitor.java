package analysis.visitor;

import analysis.model.AnalysisError;
import analysis.model.ExpressionAnalysisState;
import analysis.model.VariablesState;
import analysis.values.*;
import analysis.values.visitor.*;
import com.github.javaparser.StaticJavaParser;
import analysis.values.AnyValue;
import analysis.values.IntegerRange;
import analysis.values.PossibleValues;
import analysis.values.StringValue;
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
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedParameterDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserMethodDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserParameterDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserVariableDeclaration;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import utils.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class ExpressionVisitor implements GenericVisitor<PossibleValues, ExpressionAnalysisState> {
    private final MergeVisitor mergeVisitor;
    private final AddVisitor addVisitor;
    private final DivideVisitor divideVisitor;
    private final SubtractVisitor subtractVisitor;
    private final MultiplyVisitor multiplyVisitor;

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
            VariablesState state = arg.getVariablesState();
            state.setVariable(n, value);
            return value;
        }
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(AssignExpr n, ExpressionAnalysisState arg) {
        Expression target = n.getTarget();
        PossibleValues value = n.getValue().accept(this, arg);
        VariableUtil.setVariableFromExpression(n.getTarget(), value, arg.getVariablesState());
        return value;
    }

    @Override
    public PossibleValues visit(ArrayAccessExpr n, ExpressionAnalysisState arg) {
        PossibleValues indexValue = n.getIndex().accept(this, arg);
        PossibleValues arrayNameValue = n.getName().accept(this, arg);

        // Check null
        if (arrayNameValue.canBeNull()) {
            arg.addError(new AnalysisError("NullPointerException: " + n.getName()));
        }

        // Check valid index
        IntegerValue length;
        if (arrayNameValue instanceof ArrayValue arrayValue) {
            length = arrayValue.getLength();
        } else {
            length = ArrayValue.DEFAULT_LENGTH;
        }
        // TODO: Use boolean operators
        PossibleValues lessThanZeroIndex = indexValue.acceptAbstractOp(new RestrictLessThanVisitor(), new IntegerRange(0));
        PossibleValues greaterOrEqualLengthIndex = indexValue.acceptAbstractOp(new RestrictGreaterThanOrEqualVisitor(), length);
        if (!lessThanZeroIndex.isEmpty() || !greaterOrEqualLengthIndex.isEmpty()) {
            arg.addError(new AnalysisError("ArrayIndexOutOfBoundsException: " + n));
            // TODO: Restrict value of index variable (if it was) after this point
        }

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
                // TODO: Use boolean operators
                PossibleValues lessThanZeroSize = intDimensionValue.acceptAbstractOp(new RestrictLessThanVisitor(), new IntegerRange(0));
                if (!lessThanZeroSize.isEmpty()) {
                    arg.addError(new AnalysisError("NegativeArraySizeException: " + n));
                    // TODO: Restrict value of size after this point
                }
                PossibleValues greaterOrEqualZeroSize = intDimensionValue.acceptAbstractOp(new RestrictGreaterThanOrEqualVisitor(), new IntegerRange(0));
                if (greaterOrEqualZeroSize instanceof IntegerValue validSize) {
                    return new ArrayValue(validSize);
                }
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
        PossibleValues leftValue = n.getLeft().accept(this, arg);
        PossibleValues rightValue = n.getRight().accept(this, arg);
        return switch (n.getOperator()) {
            case DIVIDE -> {
                PairValue<PossibleValues, AnalysisError> result = leftValue.acceptAbstractOp(divideVisitor, rightValue);
                AnalysisError error = result.getB();
                if (error != null) {
                    arg.addError(error);
                }
                yield result.getA();
            }
            case PLUS -> leftValue.acceptAbstractOp(addVisitor, rightValue);
            case MINUS -> leftValue.acceptAbstractOp(subtractVisitor, rightValue);
            case MULTIPLY -> leftValue.acceptAbstractOp(multiplyVisitor, rightValue);
            default -> new AnyValue();
        };
    }

    @Override
    public PossibleValues visit(CastExpr n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ClassExpr n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ConditionalExpr n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(EnclosedExpr n, ExpressionAnalysisState arg) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(FieldAccessExpr n, ExpressionAnalysisState arg) {
        PossibleValues scopeValue = n.getScope().accept(this, arg);
        ResolvedValueDeclaration valDec = n.resolve();
        if (scopeValue instanceof ArrayValue arrayValue) {
            if (valDec.getName().equals("length")) {
                return arrayValue.getLength();
            }
        }
        // TODO: handle field access default value (+ annotations)
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(InstanceOfExpr n, ExpressionAnalysisState arg) {
        return new AnyValue();
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
            PossibleValues objectValue = object.accept(this, arg);

            if (objectValue.canBeNull()) {
                arg.addError(new AnalysisError("NullPointerException: " + n, objectValue == NullValue.VALUE));
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
                List<AnalysisError> errors = AnnotationUtil.checkArgumentWithAnnotation(expValue, annotations, n.toString());
                if (errors.size() != 0) {
                    arg.addErrors(errors);
                }
            }
        }

        // handle exceptions in Javadoc
        if (dec instanceof JavaParserMethodDeclaration methodDec) {
            MethodDeclaration methodDeclaration = methodDec.getWrappedNode();
            Set<ResolvedType> resolvedTypes = JavadocUtil.getThrows(methodDeclaration);
            for (ResolvedType rt: resolvedTypes) {
                arg.addError(new AnalysisError(methodName + " may throw " + rt.describe()));
            }
        }

        return new AnyValue();
    }

    @Override
    public PossibleValues visit(NameExpr n, ExpressionAnalysisState arg) {
        ResolvedValueDeclaration dec = n.resolve();
        VariablesState state = arg.getVariablesState();
        if (dec instanceof JavaParserVariableDeclaration jpVarDec) {
            return state.getVariable(jpVarDec.getVariableDeclarator());
        }
        if (dec instanceof JavaParserParameterDeclaration jpParamDec) {
            return state.getVariable(jpParamDec.getWrappedNode());
        }
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ObjectCreationExpr n, ExpressionAnalysisState arg) {
        return new AnyValue();
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
        if (preValue instanceof IntegerValue intValue) {
            PossibleValues postValue;
            switch (n.getOperator()) {
                case PREFIX_INCREMENT, POSTFIX_INCREMENT -> {
                    postValue = intValue.acceptAbstractOp(addVisitor, new IntegerRange(1, 1));
                    VariableUtil.setVariableFromExpression(n.getExpression(), postValue, arg.getVariablesState());
                }
                case PREFIX_DECREMENT, POSTFIX_DECREMENT -> {
                    postValue = intValue.acceptAbstractOp(subtractVisitor, new IntegerRange(1, 1));
                    VariableUtil.setVariableFromExpression(n.getExpression(), postValue, arg.getVariablesState());
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
            return switch (n.getOperator()) {
                case POSTFIX_INCREMENT, POSTFIX_DECREMENT -> preValue;
                default -> postValue;
            };
        }
        // TODO: booleans
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
