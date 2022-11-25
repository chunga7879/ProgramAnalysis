package analysis.visitor;

import analysis.model.*;
import analysis.values.AnyValue;
import analysis.values.visitor.AddApproximateVisitor;
import analysis.values.visitor.IntersectVisitor;
import analysis.values.visitor.MergeVisitor;
import analysis.values.visitor.SubtractApproximateVisitor;
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

import java.util.ArrayList;
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
        arg.diagram.addStartDiagramNode();
        dec.accept(this, arg);
        arg.diagram.addEndDiagramNode();
        return null;
    }

    @Override
    public EndState visit(MethodDeclaration n, AnalysisState arg) {
        VariablesState varState = arg.getVariablesState();
        Optional<BlockStmt> body = n.getBody();
        for (Parameter p : n.getParameters()) {
            // TODO: handle annotations for parameters
            varState.setVariable(p, new AnyValue());
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
        AnalysisLogger.logEnd(n, "IF MERGED: ", varState);
        return null;
    }

    @Override
    public EndState visit(WhileStmt n, AnalysisState arg) {
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
        ExpressionAnalysisState exprAnalysisState = new ExpressionAnalysisState(varState);

        // Gather all initializations to add them to diagram
        List<String> initializations = new ArrayList<>();
        for (Expression e : n.getInitialization()) {
            initializations.add(e.toString().replaceAll("\"", ""));
            e.accept(expressionVisitor, exprAnalysisState);
        }
        AnalysisLogger.log(n, "FOR INITIALIZE: " + varState.toFormattedString());
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
            AnalysisLogger.log(n, "FOR [" + i + "] MERGE STATE: ", mergeState);
            if (i > 1000 && !isApprox) {
                // If # of loop runs is too long, start to approximate changes
                isApprox = true;
                AnalysisLogger.log(n, "FOR [" + i + "] TOO MANY ITERATIONS");
                loopExprVisitor = new ExpressionVisitor(mergeVisitor, new AddApproximateVisitor(), new SubtractApproximateVisitor());
                loopAnalysisVisitor = new AnalysisVisitor(targetMethod, loopExprVisitor);
            }

            ExpressionAnalysisState loopExprAnalysisState = new ExpressionAnalysisState(currentState);
            if (n.getCompare().isPresent()) {
                // Add for loop node with initializations and loop condition
                arg.diagram.addForStartNode(initializations, n.getCompare().toString());
                ConditionStates condStates = n.getCompare().get().accept(conditionVisitor, loopExprAnalysisState);
                if (!condStates.getFalseState().isDomainEmpty()) {
                    exitState.merge(mergeVisitor, condStates.getFalseState());
                }
                if (condStates.getTrueState().isDomainEmpty()) break;
                currentState.copyValuesFrom(condStates.getTrueState());
                AnalysisLogger.log(n, "FOR [" + i + "] CONDITION: ", currentState);
            } else {
                // If no condition, the node should just be the empty string.
                arg.diagram.addForStartNode(initializations, "");
            }

            AnalysisLogger.log(n, "FOR [" + i + "] ITERATION START STATE: ", currentState);
            AnalysisState analysisState = new AnalysisState(currentState);
            EndState bodyEndState = n.getBody().accept(loopAnalysisVisitor, analysisState);
            // TODO: add early breaks from bodyEndState to exitState
            List<String> updates = new ArrayList<>();
            for (Expression e : n.getUpdate()) {
                updates.add(e.toString().replaceAll("\"", ""));
                e.accept(loopExprVisitor, loopExprAnalysisState);
            }
            arg.diagram.addForEndNode(updates);
            AnalysisLogger.logEnd(n, "FOR [" + i + "] ITERATION STATE: ", currentState);

            VariablesState prevState = mergeState.copy();
            mergeState.merge(mergeVisitor, currentState);
            if (Objects.equals(mergeState, prevState)) {
                AnalysisLogger.logEnd(n, "FOR [" + i + "] UNCHANGED: ", mergeState);
                break;
            }
            i++;
        } while (true);
        AnalysisLogger.logEnd(n, "FOR EXIT STATE: ", exitState);
        if (exitState.isDomainEmpty()) AnalysisLogger.logEnd(n, "FOR LOOP IS INFINITE");
        arg.getVariablesState().copyValuesFrom(exitState);
        return null;
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
