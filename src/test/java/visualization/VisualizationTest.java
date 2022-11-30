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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PositiveOrZero;

import static analysis.visitor.VisitorTestUtils.compile;
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
    public void testIfStmtThree() {
        String code = """
                public class Main {
                    int test(Integer x, Integer y) {
                        if (x == y && x >= 0) {
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

        visualizationState.getDiagram().createDiagramPNG("src/test/java/visualization/outputs/testIfStmtThree.png");
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

    @Test
    public void generalExample1() {
        String code = """
                public class Main {
                    void test(int x) {
                        while (x < 10) {
                            int y = 1234 / x;
                            x++;
                        }
                        int z = x;
                        for (int i = 0; i < 10; i++) {
                            int b = 100 / i;
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

        visualizationState.getDiagram().createDiagramPNG("src/test/java/visualization/outputs/generalExample1.png");
    }

    @Test
    public void demoExample() {
        String code = """
                public class Main {
                    void test(int x) {
                        while (x < 10) {
                            int y = 1234 / x;
                            x++;
                        }
                        int z = x;
                        for (int i = 0; i < 10; i++) {
                            int b = 100 / i;
                        }
                    }
                }
                """;
    }

    @Test
    public void introductionTest() {
        String code = """
                public class Main {
                    void test(@Positive int i) { // Annotation will have our analysis assuming value domain of i is > 0
                        for (int j = i; j < 10; j++) { // Analysis will assume value domain of j > 0 since assigned from i
                            int z = 10 / j; // Analysis will know that j is > 0
                        }
                        int error = 100 / 0; // Will produce error :(
                    }
                }
                """;

        CompilationUnit compiled = compile(code);
        Parameter x = getParameter(compiled, "i");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(x, new IntegerRange(1, 115));
        VisualizationState visualizationState = new VisualizationState(new Diagram(), analysisState.getErrorMap());

        AnalysisVisitor av = new AnalysisVisitor("test");
        VisualizationVisitor vv = new VisualizationVisitor("test");

        av.visit(compiled, analysisState);
        vv.visit(compiled, visualizationState);

        visualizationState.getDiagram().createDiagramPNG("src/test/java/visualization/outputs/introductionExample.png");

    }

    @Test
    public void demoTest() {
        String code = """
                public class Main {
                    void demo(@NotEmpty int[] nums, @PositiveOrZero int k) {
                        int sum = 0;
                        for (int i = 0; i < k; i++) {
                            sum += nums[i];
                        }
                        int avg = sum / k;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        Parameter x = getParameter(compiled, "k");
        Parameter nums = getParameter(compiled, "nums");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(x, new IntegerRange(1, 115));
        VisualizationState visualizationState = new VisualizationState(new Diagram(), analysisState.getErrorMap());

        AnalysisVisitor av = new AnalysisVisitor("demo");
        VisualizationVisitor vv = new VisualizationVisitor("demo");

        av.visit(compiled, analysisState);
        vv.visit(compiled, visualizationState);

        visualizationState.getDiagram().createDiagramPNG("src/test/java/visualization/outputs/demo.png");
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

    // Calculate average of first k-numbers in array
    void demo(@NotEmpty int[] nums, @PositiveOrZero int k) {
        int sum = 0;
        for (int i = 0; i < k; i++) {
            sum += nums[i];
        }
        int avg = sum / k;
    }

    boolean searchDemo(@NotEmpty int[] nums, @PositiveOrZero int k, int item) {
        for (int i = 0; i < k; i++) {
            if (nums[i] == item) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void testDemo() {
        String code = """
                public class Main {
                    boolean demo(@NotEmpty int[] nums, @PositiveOrZero int k, int item) {
                        int i = 0;
                        while (i < k) {
                            if (nums[i] == item) {
                                return true;
                            }
                            i++;
                        }
                        return false;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        Parameter k = getParameter(compiled, "k");
        Parameter nums = getParameter(compiled, "nums");
        Parameter item = getParameter(compiled, "item");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(k, new IntegerRange(1, Integer.MAX_VALUE));
        varState.setVariable(item , new IntegerRange(Integer.MIN_VALUE, Integer.MAX_VALUE));
        VisualizationState visualizationState = new VisualizationState(new Diagram(), analysisState.getErrorMap());

        AnalysisVisitor av = new AnalysisVisitor("demo");
        VisualizationVisitor vv = new VisualizationVisitor("demo");

        av.visit(compiled, analysisState);
        vv.visit(compiled, visualizationState);

        visualizationState.getDiagram().createDiagramPNG("src/test/java/visualization/outputs/demo2.png");
    }
}