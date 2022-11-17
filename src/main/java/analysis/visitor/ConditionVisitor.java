package analysis.visitor;

import analysis.model.ConditionStates;
import analysis.model.VariablesState;
import analysis.values.PossibleValues;
import analysis.values.visitor.IntersectVisitor;
import analysis.values.visitor.MergeVisitor;
import analysis.values.visitor.RestrictGreaterThanVisitor;
import analysis.values.visitor.RestrictLessThanOrEqualVisitor;
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
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserParameterDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserVariableDeclaration;

public class ConditionVisitor implements GenericVisitor<ConditionStates, VariablesState> {
    private ExpressionVisitor expressionVisitor;
    private MergeVisitor mergeVisitor;
    private IntersectVisitor intersectVisitor;
    private RestrictGreaterThanVisitor restrictGTVisitor;
    private RestrictLessThanOrEqualVisitor restrictLTEVisitor;

    public ConditionVisitor(ExpressionVisitor expressionVisitor,
                            MergeVisitor mergeVisitor, IntersectVisitor intersectVisitor) {
        this.expressionVisitor = expressionVisitor;
        this.mergeVisitor = mergeVisitor;
        this.intersectVisitor = intersectVisitor;
        this.restrictGTVisitor = new RestrictGreaterThanVisitor();
        this.restrictLTEVisitor = new RestrictLessThanOrEqualVisitor();
    }

    @Override
    public ConditionStates visit(BinaryExpr n, VariablesState arg) {
        Expression leftExpr = n.getLeft();
        Expression rightExpr = n.getRight();
        if (isBooleanOperator(n.getOperator())) {
            return handleBooleanOperators(leftExpr, rightExpr, n.getOperator(), arg);
        }
        PossibleValues leftValues = leftExpr.accept(expressionVisitor, arg);
        PossibleValues rightValues = rightExpr.accept(expressionVisitor, arg);
        VariablesState trueState = arg.copy();
        VariablesState falseState = arg.copy();

        switch (n.getOperator()) {
            case GREATER -> {
                PossibleValues leftTrueRestrictedValues = leftValues.acceptAbstractOp(restrictGTVisitor, rightValues);
                PossibleValues rightTrueRestrictedValues = rightValues.acceptAbstractOp(restrictLTEVisitor, leftValues);
                PossibleValues leftFalseRestrictedValues = leftValues.acceptAbstractOp(restrictLTEVisitor, rightValues);
                PossibleValues rightFalseRestrictedValues = rightValues.acceptAbstractOp(restrictGTVisitor, leftValues);
                if (leftTrueRestrictedValues.isEmpty() || rightTrueRestrictedValues.isEmpty()) trueState.setDomainEmpty();
                if (leftFalseRestrictedValues.isEmpty() || rightFalseRestrictedValues.isEmpty()) falseState.setDomainEmpty();
                if (leftExpr instanceof NameExpr leftVar) {
                    restrictNameExpr(leftVar, leftTrueRestrictedValues, leftFalseRestrictedValues, trueState, falseState);
                }
                if (rightExpr instanceof NameExpr rightVar) {
                    restrictNameExpr(rightVar, rightTrueRestrictedValues, rightFalseRestrictedValues, trueState, falseState);
                }
            }
            default -> {}
        };
        return new ConditionStates(trueState, falseState);
    }

    private boolean isBooleanOperator(BinaryExpr.Operator operator) {
        return operator.equals(BinaryExpr.Operator.AND) || operator.equals(BinaryExpr.Operator.OR);
    }

    private ConditionStates handleBooleanOperators(Expression leftExpr, Expression rightExpr,
                                                   BinaryExpr.Operator operator, VariablesState arg) {
        // TODO: handle visiting assignments
        ConditionStates leftStates = leftExpr.accept(this, arg);
        ConditionStates rightStates = rightExpr.accept(this, arg);
        return switch (operator) {
            case AND -> new ConditionStates(
                    leftStates.getTrueState().intersectCopy(intersectVisitor, rightStates.getTrueState()),
                    leftStates.getFalseState().mergeCopy(mergeVisitor, rightStates.getFalseState())
            );
            case OR -> new ConditionStates(
                    leftStates.getTrueState().mergeCopy(mergeVisitor, rightStates.getTrueState()),
                    leftStates.getFalseState().intersectCopy(intersectVisitor, rightStates.getFalseState())
            );
            default -> new ConditionStates(arg.copy(), arg.copy());
        };
    }

    private void restrictNameExpr(NameExpr nameExpr,
                                  PossibleValues trueRestrictedValues, PossibleValues falseRestrictedValues,
                                  VariablesState trueState, VariablesState falseState) {
        ResolvedValueDeclaration valDec = nameExpr.resolve();
        if (valDec instanceof JavaParserVariableDeclaration jpVarDec) {
            trueState.setVariable(jpVarDec.getVariableDeclarator(), trueRestrictedValues);
            falseState.setVariable(jpVarDec.getVariableDeclarator(), falseRestrictedValues);
        } else if (valDec instanceof JavaParserParameterDeclaration jpVarDec) {
            trueState.setVariable(jpVarDec.getWrappedNode(), trueRestrictedValues);
            falseState.setVariable(jpVarDec.getWrappedNode(), falseRestrictedValues);
        }
    }

    @Override
    public ConditionStates visit(UnaryExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(NameExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(BooleanLiteralExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(InstanceOfExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(AssignExpr n, VariablesState arg) {
        return null;
    }


    // region ----Not required----
    // Move any we're not using here
    @Override
    public ConditionStates visit(CompilationUnit n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(PackageDeclaration n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(TypeParameter n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(LineComment n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(BlockComment n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ClassOrInterfaceDeclaration n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(RecordDeclaration n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(CompactConstructorDeclaration n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(EnumDeclaration n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(EnumConstantDeclaration n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(AnnotationDeclaration n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(AnnotationMemberDeclaration n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(FieldDeclaration n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(VariableDeclarator n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ConstructorDeclaration n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(MethodDeclaration n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(Parameter n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(InitializerDeclaration n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(JavadocComment n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ClassOrInterfaceType n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(PrimitiveType n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ArrayType n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ArrayCreationLevel n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(IntersectionType n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(UnionType n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(VoidType n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(WildcardType n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(UnknownType n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ArrayAccessExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ArrayCreationExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ArrayInitializerExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(CastExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ClassExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ConditionalExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(EnclosedExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(FieldAccessExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(StringLiteralExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(IntegerLiteralExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(LongLiteralExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(CharLiteralExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(DoubleLiteralExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(NullLiteralExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(MethodCallExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ObjectCreationExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ThisExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(SuperExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(VariableDeclarationExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(MarkerAnnotationExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(SingleMemberAnnotationExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(NormalAnnotationExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(MemberValuePair n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ExplicitConstructorInvocationStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(LocalClassDeclarationStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(LocalRecordDeclarationStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(AssertStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(BlockStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(LabeledStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(EmptyStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ExpressionStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(SwitchStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(SwitchEntry n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(BreakStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ReturnStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(IfStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(WhileStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ContinueStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(DoStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ForEachStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ForStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ThrowStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(SynchronizedStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(TryStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(CatchClause n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(LambdaExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(MethodReferenceExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(TypeExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(NodeList n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(Name n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(SimpleName n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ImportDeclaration n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ModuleDeclaration n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ModuleRequiresDirective n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ModuleExportsDirective n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ModuleProvidesDirective n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ModuleUsesDirective n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ModuleOpensDirective n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(UnparsableStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ReceiverParameter n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(VarType n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(Modifier n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(SwitchExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(YieldStmt n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(TextBlockLiteralExpr n, VariablesState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(PatternExpr n, VariablesState arg) {
        return null;
    }
    // endregion ----Not required----
}
