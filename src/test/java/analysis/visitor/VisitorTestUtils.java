package analysis.visitor;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.util.List;
import java.util.Objects;

public final class VisitorTestUtils {
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
}
