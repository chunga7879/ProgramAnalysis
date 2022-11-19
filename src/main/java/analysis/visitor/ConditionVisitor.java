package analysis.visitor;

import analysis.model.ConditionStates;
import analysis.model.ExpressionAnalysisState;
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

public class ConditionVisitor implements GenericVisitor<ConditionStates, ExpressionAnalysisState> {
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
    public ConditionStates visit(BinaryExpr n, ExpressionAnalysisState arg) {
        Expression leftExpr = n.getLeft();
        Expression rightExpr = n.getRight();
        if (isBooleanOperator(n.getOperator())) {
            return handleBooleanOperators(leftExpr, rightExpr, n.getOperator(), arg);
        }
        PossibleValues leftValues = leftExpr.accept(expressionVisitor, arg);
        PossibleValues rightValues = rightExpr.accept(expressionVisitor, arg);
        VariablesState state = arg.getVariablesState();
        VariablesState trueState = state.copy();
        VariablesState falseState = state.copy();

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
                                                   BinaryExpr.Operator operator, ExpressionAnalysisState arg) {
        // TODO: handle visiting assignments
        ConditionStates leftStates = leftExpr.accept(this, arg);
        ConditionStates rightStates = rightExpr.accept(this, arg);
        VariablesState state = arg.getVariablesState();
        return switch (operator) {
            case AND -> new ConditionStates(
                    leftStates.getTrueState().intersectCopy(intersectVisitor, rightStates.getTrueState()),
                    leftStates.getFalseState().mergeCopy(mergeVisitor, rightStates.getFalseState())
            );
            case OR -> new ConditionStates(
                    leftStates.getTrueState().mergeCopy(mergeVisitor, rightStates.getTrueState()),
                    leftStates.getFalseState().intersectCopy(intersectVisitor, rightStates.getFalseState())
            );
            default -> new ConditionStates(state.copy(), state.copy());
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
    public ConditionStates visit(UnaryExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(NameExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(BooleanLiteralExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(InstanceOfExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(AssignExpr n, ExpressionAnalysisState arg) {
        return null;
    }


    // region ----Not required----
    // Move any we're not using here
    @Override
    public ConditionStates visit(CompilationUnit n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(PackageDeclaration n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(TypeParameter n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(LineComment n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(BlockComment n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ClassOrInterfaceDeclaration n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(RecordDeclaration n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(CompactConstructorDeclaration n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(EnumDeclaration n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(EnumConstantDeclaration n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(AnnotationDeclaration n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(AnnotationMemberDeclaration n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(FieldDeclaration n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(VariableDeclarator n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ConstructorDeclaration n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(MethodDeclaration n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(Parameter n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(InitializerDeclaration n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(JavadocComment n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ClassOrInterfaceType n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(PrimitiveType n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ArrayType n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ArrayCreationLevel n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(IntersectionType n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(UnionType n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(VoidType n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(WildcardType n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(UnknownType n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ArrayAccessExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ArrayCreationExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ArrayInitializerExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(CastExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ClassExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ConditionalExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(EnclosedExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(FieldAccessExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(StringLiteralExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(IntegerLiteralExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(LongLiteralExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(CharLiteralExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(DoubleLiteralExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(NullLiteralExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(MethodCallExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ObjectCreationExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ThisExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(SuperExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(VariableDeclarationExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(MarkerAnnotationExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(SingleMemberAnnotationExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(NormalAnnotationExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(MemberValuePair n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ExplicitConstructorInvocationStmt n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(LocalClassDeclarationStmt n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(LocalRecordDeclarationStmt n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(AssertStmt n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(BlockStmt n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(LabeledStmt n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(EmptyStmt n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ExpressionStmt n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(SwitchStmt n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(SwitchEntry n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(BreakStmt n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ReturnStmt n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(IfStmt n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(WhileStmt n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ContinueStmt n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(DoStmt n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ForEachStmt n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ForStmt n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ThrowStmt n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(SynchronizedStmt n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(TryStmt n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(CatchClause n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(LambdaExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(MethodReferenceExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(TypeExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(NodeList n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(Name n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(SimpleName n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ImportDeclaration n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ModuleDeclaration n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ModuleRequiresDirective n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ModuleExportsDirective n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ModuleProvidesDirective n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ModuleUsesDirective n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ModuleOpensDirective n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(UnparsableStmt n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(ReceiverParameter n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(VarType n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(Modifier n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(SwitchExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(YieldStmt n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(TextBlockLiteralExpr n, ExpressionAnalysisState arg) {
        return null;
    }

    @Override
    public ConditionStates visit(PatternExpr n, ExpressionAnalysisState arg) {
        return null;
    }
    // endregion ----Not required----
}
