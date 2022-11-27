package analysis.visitor;

import analysis.model.*;
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
import logger.AnalysisLogger;
import utils.ValueUtil;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AnalysisVisitor implements GenericVisitor<EndState, AnalysisState> {
    private final String targetMethod;
    private final ExpressionVisitor expressionVisitor;
    private final ConditionVisitor conditionVisitor;
    private final MergeVisitor mergeVisitor;
    private final IntersectVisitor intersectVisitor;

    public AnalysisVisitor(String targetMethod) {
        this(targetMethod, new ExpressionVisitor());
    }

    public AnalysisVisitor(String targetMethod, ExpressionVisitor expressionVisitor) {
        this.targetMethod = targetMethod;
        this.mergeVisitor = new MergeVisitor();
        this.intersectVisitor = new IntersectVisitor();
        this.expressionVisitor = expressionVisitor;
        this.conditionVisitor = new ConditionVisitor(
                this.expressionVisitor,
                this.mergeVisitor,
                this.intersectVisitor
        );
    }

    @Override
    public EndState visit(CompilationUnit n, AnalysisState arg) {
        List<MethodDeclaration> declarations = n.findAll(
                MethodDeclaration.class,
                methodDeclaration -> Objects.equals(methodDeclaration.getNameAsString(), targetMethod)
        );
        if (declarations.isEmpty()) throw new RuntimeException("Method not found");
        MethodDeclaration dec = declarations.get(0);
        dec.accept(this, arg);
        return null;
    }

    @Override
    public EndState visit(MethodDeclaration n, AnalysisState arg) {
        VariablesState varState = arg.getVariablesState();
        Optional<BlockStmt> body = n.getBody();
        for (Parameter p : n.getParameters()) {
            // TODO: handle annotations for parameters
            PossibleValues val = ValueUtil.getValueForType(p.getType().resolve(), p.getAnnotations(), arg.getVariablesState(), expressionVisitor);
            varState.setVariable(p, val);
        }
        AnalysisLogger.log(n.getName(), varState);
        return body.map(blockStmt -> blockStmt.accept(this, arg)).orElse(null);
    }

    @Override
    public EndState visit(BlockStmt n, AnalysisState arg) {
        for (Statement s : n.getStatements()) {
            s.accept(this, arg);
        }
        return null;
    }

    @Override
    public EndState visit(ExpressionStmt n, AnalysisState arg) {
        VariablesState varState = arg.getVariablesState();
        ExpressionAnalysisState exprAnalysisState = new ExpressionAnalysisState(varState);
        n.getExpression().accept(expressionVisitor, exprAnalysisState);
        arg.addErrors(n, exprAnalysisState.getErrors());
        AnalysisLogger.log(n, varState, exprAnalysisState.getErrors());
        return null;
    }

    @Override
    public EndState visit(SwitchStmt n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(SwitchEntry n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(BreakStmt n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(ReturnStmt n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(IfStmt n, AnalysisState arg) {
        VariablesState varState = arg.getVariablesState();
        ExpressionAnalysisState exprAnalysisState = new ExpressionAnalysisState(varState);
        ConditionStates conditionStates = n.getCondition().accept(conditionVisitor, exprAnalysisState);
        VariablesState trueVarState = conditionStates.getTrueState();
        VariablesState falseVarState = conditionStates.getFalseState();
        AnalysisLogger.logFormat(n, "IF: ", varState);
        AnalysisLogger.logErrors(n, exprAnalysisState.getErrors());
        arg.addErrors(n, exprAnalysisState.getErrors());

        // IF case
        AnalysisLogger.log(n, "IF TRUE: " + trueVarState.toFormattedString());
        AnalysisState trueAnalysisState = new AnalysisState(trueVarState);
        n.getThenStmt().accept(this, trueAnalysisState);

        // ELSE case
        AnalysisLogger.log(n, "IF FALSE: " + falseVarState.toFormattedString());
        AnalysisState falseAnalysisState = new AnalysisState(falseVarState);
        if (n.getElseStmt().isPresent()) {
            n.getElseStmt().get().accept(this, falseAnalysisState);
        }

        // Merge together
        VariablesState mergedState = new VariablesState();
        if (!trueVarState.isDomainEmpty()) mergedState.copyValuesFrom(trueVarState);
        if (!falseVarState.isDomainEmpty()) mergedState.merge(mergeVisitor, falseVarState);
        varState.copyValuesFrom(mergedState);
        AnalysisLogger.logEndFormat(n, "IF MERGED: %s", varState);
        return null;
    }

    @Override
    public EndState visit(WhileStmt n, AnalysisState arg) {
        handleLoop(n, "WHILE", arg, n.getCondition(), n.getBody(), null);
        return null;
    }

    @Override
    public EndState visit(ContinueStmt n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(DoStmt n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(ForEachStmt n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(ForStmt n, AnalysisState arg) {
        VariablesState varState = arg.getVariablesState();
        for (Expression e : n.getInitialization()) {
            ExpressionAnalysisState exprAnalysisState = new ExpressionAnalysisState(varState);
            e.accept(expressionVisitor, exprAnalysisState);
            arg.addErrors(e, exprAnalysisState.getErrors());
        }
        AnalysisLogger.log(n, "FOR INITIALIZE: " + varState.toFormattedString());

        handleLoop(n, "FOR", arg, n.getCompare().orElse(null), n.getBody(), n.getUpdate());
        return null;
    }

    /**
     * Handle a loop statement
     * @param loopNode Node containing the loop
     * @param loopName Name of the loop
     * @param state    AnalysisState at the loop
     * @param compare  Condition to continue loop
     * @param body     Body inside the loop
     * @param update   Updater (i.e. for loop)
     */
    private void handleLoop(Node loopNode, String loopName, AnalysisState state, Expression compare, Statement body, NodeList<Expression> update) {
        VariablesState varState = state.getVariablesState();
        VariablesState mergeState = varState.copy(); // State tracking the values in all iterations
        VariablesState currentState = mergeState.copy(); // State tracking the values in each iteration
        VariablesState exitState = new VariablesState(); // State tracking the values when the loop exits
        exitState.setDomainEmpty();

        // Visitors for this loop
        AnalysisVisitor loopAnalysisVisitor = this;
        ExpressionVisitor loopExprVisitor = this.expressionVisitor;

        int i = 0;
        boolean isApprox = false;
        do {
            AnalysisLogger.logFormat(loopNode, "%s [%s] MERGE STATE: %s", loopName, i, mergeState);
            if (i > 1000 && !isApprox) {
                // If # of loop runs is too long, start to approximate changes
                isApprox = true;
                AnalysisLogger.logFormat(loopNode, "%s [%s] TOO MANY ITERATIONS", loopName, i);
                loopExprVisitor = new ExpressionVisitor(mergeVisitor, new AddApproximateVisitor(), new DivideVisitor(),
                        new MultiplyVisitor(), new SubtractApproximateVisitor());
                loopAnalysisVisitor = new AnalysisVisitor(targetMethod, loopExprVisitor);
            }

            if (compare != null) {
                ExpressionAnalysisState compareAnalysisState = new ExpressionAnalysisState(currentState);
                ConditionStates condStates = compare.accept(conditionVisitor, compareAnalysisState);
                state.addErrors(compare, compareAnalysisState.getErrors());
                if (!condStates.getFalseState().isDomainEmpty()) {
                    exitState.merge(mergeVisitor, condStates.getFalseState());
                    AnalysisLogger.logFormat(loopNode, "%s [%s] EXIT STATE: %s", loopName, i, exitState);
                }
                if (condStates.getTrueState().isDomainEmpty()) break;
                currentState.copyValuesFrom(condStates.getTrueState());
                AnalysisLogger.logFormat(loopNode, "%s [%s] CONDITION: %s", loopName, i, currentState);
            }

            AnalysisLogger.logFormat(loopNode, "%s [%s] ITERATION START STATE: %s", loopName, i, currentState);
            AnalysisState analysisState = new AnalysisState(currentState);
            EndState bodyEndState = body.accept(loopAnalysisVisitor, analysisState);
            state.addErrors(analysisState);
            // TODO: add early breaks from bodyEndState to exitState
            if (update != null) {
                for (Expression e : update) {
                    ExpressionAnalysisState updateAnalysisState = new ExpressionAnalysisState(currentState);
                    e.accept(loopExprVisitor, updateAnalysisState);
                    state.addErrors(e, updateAnalysisState.getErrors());
                }
            }
            AnalysisLogger.logEndFormat(loopNode, "%s [%s] ITERATION STATE: %s", loopName, i, currentState);

            VariablesState prevState = mergeState.copy();
            mergeState.merge(mergeVisitor, currentState);
            if (Objects.equals(mergeState, prevState)) {
                AnalysisLogger.logEndFormat(loopNode, "%s [%s] UNCHANGED: %s", loopName, i, mergeState);
                break;
            }
            i++;
        } while (true);
        AnalysisLogger.logEndFormat(loopNode, "%s EXIT STATE: %s", loopName, exitState);
        if (exitState.isDomainEmpty()) AnalysisLogger.logEndFormat(loopNode, "%s LOOP IS INFINITE", loopName);
        varState.copyValuesFrom(exitState);
    }

    @Override
    public EndState visit(ThrowStmt n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(TryStmt n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(CatchClause n, AnalysisState arg) {
        return null;
    }


    // region ----Not required----
    // Move any we're not using here
    @Override
    public EndState visit(Name n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(SimpleName n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(ArrayAccessExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(ArrayCreationExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(ArrayInitializerExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(AssignExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(BinaryExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(CastExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(ClassExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(ConditionalExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(EnclosedExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(FieldAccessExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(InstanceOfExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(StringLiteralExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(IntegerLiteralExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(LongLiteralExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(CharLiteralExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(DoubleLiteralExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(BooleanLiteralExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(NullLiteralExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(MethodCallExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(NameExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(ObjectCreationExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(ThisExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(SuperExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(UnaryExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(VariableDeclarationExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(LabeledStmt n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(EmptyStmt n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(NodeList n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(PackageDeclaration n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(TypeParameter n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(LineComment n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(BlockComment n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(ClassOrInterfaceDeclaration n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(RecordDeclaration n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(CompactConstructorDeclaration n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(EnumDeclaration n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(EnumConstantDeclaration n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(AnnotationDeclaration n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(AnnotationMemberDeclaration n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(FieldDeclaration n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(VariableDeclarator n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(ConstructorDeclaration n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(Parameter n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(InitializerDeclaration n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(JavadocComment n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(ClassOrInterfaceType n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(PrimitiveType n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(ArrayType n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(ArrayCreationLevel n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(IntersectionType n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(MarkerAnnotationExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(SingleMemberAnnotationExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(NormalAnnotationExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(MemberValuePair n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(ExplicitConstructorInvocationStmt n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(LocalClassDeclarationStmt n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(LocalRecordDeclarationStmt n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(AssertStmt n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(UnionType n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(VoidType n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(WildcardType n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(UnknownType n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(SynchronizedStmt n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(LambdaExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(MethodReferenceExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(TypeExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(ImportDeclaration n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(ModuleDeclaration n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(ModuleRequiresDirective n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(ModuleExportsDirective n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(ModuleProvidesDirective n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(ModuleUsesDirective n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(ModuleOpensDirective n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(UnparsableStmt n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(ReceiverParameter n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(VarType n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(Modifier n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(SwitchExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(YieldStmt n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(TextBlockLiteralExpr n, AnalysisState arg) {
        return null;
    }

    @Override
    public EndState visit(PatternExpr n, AnalysisState arg) {
        return null;
    }
    // endregion ----Not required----
}
