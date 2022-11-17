package demo;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JavaParserDemo {
    private final static String LOCATION = "src/test/java/demo/";
    public static void main(String[] args) throws IOException {
        StaticJavaParser.getParserConfiguration().setSymbolResolver(new JavaSymbolSolver(new ReflectionTypeSolver()));
        Path path = Paths.get(LOCATION + "ArithmeticExceptionExample.java");
        CompilationUnit compilationUnit = StaticJavaParser.parse(path);
        compilationUnit.accept(new AstVisitor("divisionByZero"), null);

        path = Paths.get(LOCATION + "MethodCallExample.java");
        compilationUnit = StaticJavaParser.parse(path);
        compilationUnit.accept(new AstVisitor("passNullToMethod"), null);
    }
}
