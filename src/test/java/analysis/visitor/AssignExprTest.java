package analysis.visitor;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import analysis.values.IntegerRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.VariableDeclarator;
import logger.AnalysisLogger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static analysis.visitor.VisitorTestUtils.compile;
import static analysis.visitor.VisitorTestUtils.getVariable;

public class AssignExprTest {
    private VariablesState variablesState;
    private AnalysisState analysisState;

    @BeforeEach
    public void runBefore() {
        variablesState = new VisitorTestUtils.NonEmptyVariablesState();
        analysisState = new AnalysisState(variablesState);
        AnalysisLogger.setLog(true);
    }

    @Test
    public void addAssignmentTest() {
        String code = """
                public class Main {
                    int test() {
                        int x = 100;
                        x += 200;
                        return x;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        VariableDeclarator x = getVariable(compiled, "x");
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        IntegerRange sum = (IntegerRange) variablesState.getVariable(x);
        Assertions.assertEquals(100 + 200, sum.getMin());
        Assertions.assertEquals(100 + 200, sum.getMax());
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void divideByNonZeroAssignmentTest() {
        String code = """
                public class Main {
                    int test() {
                        int x = 10;
                        x /= 5;
                        return x;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        VariableDeclarator x = getVariable(compiled, "x");
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        IntegerRange quotient = (IntegerRange) variablesState.getVariable(x);
        Assertions.assertEquals(10 / 5, quotient.getMin());
        Assertions.assertEquals(10 / 5, quotient.getMax());
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void multiplyAssignmentTest() {
        String code = """
                public class Main {
                    int test() {
                        int x = 10;
                        x *= 20;
                        return x;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        VariableDeclarator x = getVariable(compiled, "x");
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        IntegerRange product = (IntegerRange) variablesState.getVariable(x);
        Assertions.assertEquals(10 * 20, product.getMin());
        Assertions.assertEquals(10 * 20, product.getMax());
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void subtractAssignmentTest() {
        String code = """
                public class Main {
                    int test() {
                        int x = 10;
                        x -= 6;
                        return x;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        VariableDeclarator x = getVariable(compiled, "x");
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        IntegerRange diff = (IntegerRange) variablesState.getVariable(x);
        Assertions.assertEquals(10 - 6, diff.getMin());
        Assertions.assertEquals(10 - 6, diff.getMax());
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }
}
