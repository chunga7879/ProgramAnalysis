package visualization;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import analysis.values.IntegerRange;
import analysis.visitor.AnalysisVisitor;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.Parameter;
import logger.AnalysisLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import visualization.model.VisualizationState;
import visualization.visitor.VisualizationVisitor;

import static analysis.visitor.VisitorTestUtils.*;
import static analysis.visitor.VisitorTestUtils.getParameter;

public class VisualizationTest {

    @BeforeEach
    public void runBefore() {
        AnalysisLogger.setLog(true);
    }


    @Test
    public void testMethodDeclaration() {
        String code = """
                public class Main {
                    int test(int x) {
                        if (x > 5) {
                            x = 2;
                        } else {
                            x = 5;
                        }
                        return x;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        Parameter x = getParameter(compiled, "x");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        VisualizationState visualizationState = new VisualizationState(new Diagram(), analysisState.getErrorMap());
        varState.setVariable(x, new IntegerRange(-33, 115));
        AnalysisVisitor av = new AnalysisVisitor("test");
        VisualizationVisitor vv = new VisualizationVisitor("test");

        av.visit(compiled, analysisState);
        vv.visit(compiled, visualizationState);

        visualizationState.getDiagram().createDiagramPNG("src/test/java/visualization/outputs/methodDeclarationTest1.png");
    }

    @Test
    public void testExpressionStmt() {
        String code = """
                public class Main {
                    int test(int x) {
                        int b = 1;
                        int y = 2 / x;
                        return x;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        Parameter x = getParameter(compiled, "x");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        VisualizationState visualizationState = new VisualizationState(new Diagram(), analysisState.getErrorMap());
        varState.setVariable(x, new IntegerRange(-33, 115));
        AnalysisVisitor av = new AnalysisVisitor("test");
        VisualizationVisitor vv = new VisualizationVisitor("test");

        av.visit(compiled, analysisState);
        vv.visit(compiled, visualizationState);

        visualizationState.getDiagram().createDiagramPNG("src/test/java/visualization/outputs/testExpressionStmt.png");
    }

    @Test
    public void testIfStmt() {
        String code = """
                public class Main {
                    int test(int x, int y) {
                        if (x == y && x >= 0 && !(x > 50)) {
                            x = x + 2;
                        } else {
                            x = 5;
                        }
                        return x;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        Parameter x = getParameter(compiled, "x");
        Parameter y = getParameter(compiled, "y");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(x, new IntegerRange(-33, 115));
        varState.setVariable(y, new IntegerRange(-5, 300));
        VisualizationState visualizationState = new VisualizationState(new Diagram(), analysisState.getErrorMap());

        AnalysisVisitor av = new AnalysisVisitor("test");
        VisualizationVisitor vv = new VisualizationVisitor("test");

        av.visit(compiled, analysisState);
        vv.visit(compiled, visualizationState);

        visualizationState.getDiagram().createDiagramPNG("src/test/java/visualization/outputs/testIfStmt.png");
    }

    @Test
    public void testIfStmtTwo() {
        String code = """
                public class Main {
                    int test(int x, int y) {
                        if (x == y && x >= 0 && !(x > 50)) {
                            x = x + 2;
                        }
                        return x;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        Parameter x = getParameter(compiled, "x");
        Parameter y = getParameter(compiled, "y");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(x, new IntegerRange(-33, 115));
        varState.setVariable(y, new IntegerRange(-5, 300));
        VisualizationState visualizationState = new VisualizationState(new Diagram(), analysisState.getErrorMap());

        AnalysisVisitor av = new AnalysisVisitor("test");
        VisualizationVisitor vv = new VisualizationVisitor("test");

        av.visit(compiled, analysisState);
        vv.visit(compiled, visualizationState);

        visualizationState.getDiagram().createDiagramPNG("src/test/java/visualization/outputs/testIfStmtTwo.png");
    }

    @Test
    public void testReturnStmt() {
        String code = """
                public class Main {
                    int test(int x) {
                        int b = 1;
                        int y = 2 / b;
                        return x * b;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        Parameter x = getParameter(compiled, "x");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        VisualizationState visualizationState = new VisualizationState(new Diagram(), analysisState.getErrorMap());
        varState.setVariable(x, new IntegerRange(-33, 115));

        AnalysisVisitor av = new AnalysisVisitor("test");
        VisualizationVisitor vv = new VisualizationVisitor("test");

        av.visit(compiled, analysisState);
        vv.visit(compiled, visualizationState);

        visualizationState.getDiagram().createDiagramPNG("src/test/java/visualization/outputs/testReturnStmt.png");
    }

    @Test
    public void testForLoop() {
        String code = """
                public class Main {
                    void test(int x) {
                        for (int i = 0; i < 10; i++) {
                            int y = 2 / i;
                        }
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        Parameter x = getParameter(compiled, "x");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(x, new IntegerRange(-33, 115));
        VisualizationState visualizationState = new VisualizationState(new Diagram(), analysisState.getErrorMap());

        AnalysisVisitor av = new AnalysisVisitor("test");
        VisualizationVisitor vv = new VisualizationVisitor("test");

        av.visit(compiled, analysisState);
        vv.visit(compiled, visualizationState);

        visualizationState.getDiagram().createDiagramPNG("src/test/java/visualization/outputs/testForLoop.png");
    }

    @Test
    public void testForEachStmt() {
        String code = """
                public class Main {
                    void test(int[] x) {
                        for (int i : x) {
                            int y = 2 / i;
                        }
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        Parameter x = getParameter(compiled, "x");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(x, new IntegerRange(-33, 115));
        VisualizationState visualizationState = new VisualizationState(new Diagram(), analysisState.getErrorMap());

        AnalysisVisitor av = new AnalysisVisitor("test");
        VisualizationVisitor vv = new VisualizationVisitor("test");

        av.visit(compiled, analysisState);
        vv.visit(compiled, visualizationState);

        visualizationState.getDiagram().createDiagramPNG("src/test/java/visualization/outputs/testForEachLoop.png");
    }

    @Test
    public void testWhileStmt() {
        String code = """
                public class Main {
                    void test(int x) {
                        while (x < 10) {
                            int y = x / 0;
                            x++;
                        }
                        int z = x;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        Parameter x = getParameter(compiled, "x");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(x, new IntegerRange(-33, 115));
        VisualizationState visualizationState = new VisualizationState(new Diagram(), analysisState.getErrorMap());

        AnalysisVisitor av = new AnalysisVisitor("test");
        VisualizationVisitor vv = new VisualizationVisitor("test");

        av.visit(compiled, analysisState);
        vv.visit(compiled, visualizationState);

        visualizationState.getDiagram().createDiagramPNG("src/test/java/visualization/outputs/testWhileLoop.png");
    }

    @Test
    public void testDoWhile() {
        String code = """
                public class Main {
                    void test(int x) {
                        do {
                            int y = 1324 / x;
                            x++;
                        } while (x < 10);
                        int z = x;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        Parameter x = getParameter(compiled, "x");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(x, new IntegerRange(-33, 115));
        VisualizationState visualizationState = new VisualizationState(new Diagram(), analysisState.getErrorMap());

        AnalysisVisitor av = new AnalysisVisitor("test");
        VisualizationVisitor vv = new VisualizationVisitor("test");

        av.visit(compiled, analysisState);
        vv.visit(compiled, visualizationState);

        visualizationState.getDiagram().createDiagramPNG("src/test/java/visualization/outputs/testDoWhileLoop.png");

    }
//
//    @Test
//    public void testThrowStat() {
//        String code = """
//                public class Main {
//                    int test(int x) {
//                        int b = 0;
//                        if (b == 0) {
//                            throw new ArithmeticException("divide by zero");
//                        }
//                        int y = 2 / b;
//                        return x * b;
//                    }
//                }
//                """;
//        CompilationUnit compiled = compile(code);
//        Parameter x = getParameter(compiled, "x");
//        VariablesState varState = new VariablesState();
//        AnalysisState analysisState = new AnalysisState(varState);
//        analysisState.diagram = new Diagram();
//        varState.setVariable(x, new IntegerRange(-33, 115));
//        AnalysisVisitor av = new AnalysisVisitor("test");
//
//        av.visit(compiled, analysisState);
//
//        analysisState.diagram.createDiagramPNG("src/test/java/visualization/outputs/throwStat.png");
//    }

    @Test
    public void testBreakStatement() {
        String code = """
                public class Main {
                    int test(int x) {
                        while (x < 10) {
                            int y = x / 5;
                            if (y == 2) { 
                                break;
                            }
                            x++;
                        }
                        return x;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        Parameter x = getParameter(compiled, "x");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(x, new IntegerRange(-33, 115));
        VisualizationState visualizationState = new VisualizationState(new Diagram(), analysisState.getErrorMap());

        AnalysisVisitor av = new AnalysisVisitor("test");
        VisualizationVisitor vv = new VisualizationVisitor("test");

        av.visit(compiled, analysisState);
        vv.visit(compiled, visualizationState);

        visualizationState.getDiagram().createDiagramPNG("src/test/java/visualization/outputs/testBreakStatement.png");
    }

    @Test
    public void testContinueStatement() {
        String code = """
                    public class Main {
                        int test(int x) {
                            while (x < 10) {
                                int y = x / 5;
                                if (y == 2) {
                                    continue;
                                }
                                x++;
                            }
                            return x;
                        }
                    }
                    """;
        CompilationUnit compiled = compile(code);
        Parameter x = getParameter(compiled, "x");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(x, new IntegerRange(-33, 115));
        VisualizationState visualizationState = new VisualizationState(new Diagram(), analysisState.getErrorMap());

        AnalysisVisitor av = new AnalysisVisitor("test");
        VisualizationVisitor vv = new VisualizationVisitor("test");

        av.visit(compiled, analysisState);
        vv.visit(compiled, visualizationState);

        visualizationState.getDiagram().createDiagramPNG("src/test/java/visualization/outputs/testContinue.png");
    }


}