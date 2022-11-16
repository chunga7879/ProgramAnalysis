package ui;

import analysis.model.VariablesState;
import analysis.visitor.AnalysisVisitor;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.Problem;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) throw new IOException("Requires: [Java file path] [Method name]");
        String filePath = args[0];
        String method = args[1];
        StaticJavaParser.getParserConfiguration().setSymbolResolver(new JavaSymbolSolver(new ReflectionTypeSolver()));
        CompilationUnit compilationUnit;
        try {
            System.out.println("Compiling...");
            Path path = Paths.get(filePath);
            compilationUnit = StaticJavaParser.parse(path);
            System.out.println("Finished compiling");
            try {
                System.out.println("Starting analysis...");
                compilationUnit.accept(new AnalysisVisitor(method), new VariablesState());
                System.out.println("Finished analysis");
            } catch (Exception e) {
                System.err.println("Analysis error: " + e.getMessage());
            }
        } catch (InvalidPathException | IOException e) {
            System.err.println("Invalid File Path: " + filePath);
        } catch (ParseProblemException e) {
            System.err.println("Java file could not be parsed: " + e.getProblems().size() + " Problems");
            List<Problem> problems = e.getProblems();
            for (int i = 0; i < problems.size(); i++) {
                Problem p = problems.get(i);
                System.err.println((i+1) + ". " + p.getVerboseMessage());
            }
        }
    }
}
