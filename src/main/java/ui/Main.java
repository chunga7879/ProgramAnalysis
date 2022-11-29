package ui;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import analysis.visitor.AnalysisVisitor;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.Problem;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import logger.AnalysisLogger;
import visualization.model.VisualizationState;
import visualization.visitor.VisualizationVisitor;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) throw new IOException("Requires: [Java file path] [Method name] [Output file path (optional)] -d");
        String filePath = args[0];
        String method = args[1];
        String output = "output.png";
        if (args.length >= 3) {
            if (!args[2].startsWith("-")) output = args[2];
            Set<String> otherArgs = Set.of(Arrays.copyOfRange(args, 2, args.length));
            if (otherArgs.contains("-d")) AnalysisLogger.setLog(true);
        }
        StaticJavaParser.getParserConfiguration().setSymbolResolver(new JavaSymbolSolver(new ReflectionTypeSolver()));
        CompilationUnit compilationUnit;
        try {
            System.out.println("Compiling...");
            Path path = Paths.get(filePath);
            compilationUnit = StaticJavaParser.parse(path);
            System.out.println("Finished compiling");
            try {
                System.out.println("Starting analysis...");
                AnalysisState analysisState = new AnalysisState(new VariablesState());
                compilationUnit.accept(new AnalysisVisitor(method), analysisState);
                System.out.println("Finished analysis");
                VisualizationState visualizationState = new VisualizationState(analysisState.getErrorMap());
                compilationUnit.accept(new VisualizationVisitor(method), visualizationState);
                visualizationState.diagram.createDiagramPNG(output);
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
