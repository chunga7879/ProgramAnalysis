package analysis.visitor;

import analysis.model.AnalysisError;
import analysis.model.VariablesState;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public final class VisitorTestUtils {
    public static class NonEmptyVariablesState extends VariablesState {
        @Override
        public boolean isDomainEmpty() {
            return false;
        }
    }

    public static CompilationUnit compile(String code) {
        StaticJavaParser.getParserConfiguration().setSymbolResolver(new JavaSymbolSolver(new ReflectionTypeSolver()));
        return StaticJavaParser.parse(code);
    }

    public static Parameter getParameter(CompilationUnit compilationUnit, String paramName) {
        return compilationUnit.findFirst(
                Parameter.class,
                parameter -> Objects.equals(parameter.getNameAsString(), paramName)).orElseThrow();
    }

    public static VariableDeclarator getVariable(CompilationUnit compilationUnit, String variableName) {
        return compilationUnit.findFirst(
                VariableDeclarator.class,
                variable -> Objects.equals(variable.getNameAsString(), variableName)).orElseThrow();
    }

    public static List<IfStmt> getIfStatements(CompilationUnit compilationUnit) {
        return compilationUnit.findAll(IfStmt.class);
    }

    public static List<ForStmt> getForStatements(CompilationUnit compilationUnit) {
        return compilationUnit.findAll(ForStmt.class);
    }

    public static List<WhileStmt> getWhileStatements(CompilationUnit compilationUnit) {
        return compilationUnit.findAll(WhileStmt.class);
    }

    public static List<BlockStmt> getBlockStatements(CompilationUnit compilationUnit) {
        return compilationUnit.findAll(BlockStmt.class);
    }

    public static Set<AnalysisError> getErrors(
            Map<Node, Set<AnalysisError>> errorMap,
            Function<Node, Boolean> matchFun

    ) {
        Set<Node> errorNodes = errorMap.keySet();
        Node errorNode = errorNodes.stream().filter(matchFun::apply).findFirst().orElse(null);
        if (errorNode == null) return null;
        return errorMap.get(errorNode);
    }

    public static Set<AnalysisError> getVariableDeclarationErrors(
            Map<Node, Set<AnalysisError>> errorMap,
            String decName
    ) {
        return getErrors(errorMap, (x) ->
                x instanceof ExpressionStmt expressionStmt &&
                        expressionStmt.getExpression() instanceof VariableDeclarationExpr varDecExpr &&
                        varDecExpr.getVariables().stream().anyMatch(v -> Objects.equals(v.getNameAsString(), decName)));
    }

    public static Set<AnalysisError> getVariableAssignmentErrors(
            Map<Node, Set<AnalysisError>> errorMap,
            String targetName
    ) {
        return getErrors(errorMap, (x) ->
                x instanceof ExpressionStmt expressionStmt &&
                        expressionStmt.getExpression() instanceof AssignExpr assign &&
                        assign.getTarget().isNameExpr() && Objects.equals(assign.getTarget().asNameExpr().getNameAsString(), targetName));
    }
}
