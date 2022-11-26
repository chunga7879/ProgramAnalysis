package analysis.visitor;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import analysis.values.EmptyValue;
import analysis.values.IntegerRange;
import analysis.values.PossibleValues;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.BinaryExpr;
import logger.AnalysisLogger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static analysis.visitor.VisitorTestUtils.*;

public class BinaryExprTest {

    @BeforeEach
    public void runBefore() {
        AnalysisLogger.setLog(true);
    }

    /**
     * a.min > 0, a.max > 0, b.min < 0, b.max > 0
     *
     * min = a.max / b.min
     * max = a.max / b.max
     */
    @Test
    public void divideByZeroInRange1() {
        String code = """
                public class Main {
                    int test(int x, int y) {
                        int x = x / y;
                        return x;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        BinaryExpr binaryExpr = getBinaryExpressions(compiled).get(0);
        Parameter x = getParameter(compiled, "x");
        Parameter y = getParameter(compiled, "y");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(x, new IntegerRange(2, 40));
        varState.setVariable(y, new IntegerRange(-4, 20));
        binaryExpr.accept(new AnalysisVisitor("x"), analysisState);
        IntegerRange quotient = (IntegerRange) varState.getVariable(x);
        // TODO: fix variable assignment
        Assertions.assertEquals(40 / -4, quotient.getMin());
        Assertions.assertEquals(40 / 20, quotient.getMax());
        Assertions.assertEquals(1, analysisState.getErrorMap().size());
    }

    /**
     * a.min < 0, a.max > 0, b.min < 0, b.max > 0
     *
     * min = min(a.min / b.max, a.max / b.min)
     * max = max(a.min / b.min, a.max / b.max)
     */
    @Test
    public void divideByZeroInRange2() {
        String code = """
                public class Main {
                    int test(int x, int y) {
                        int x = x / y;
                        return x;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        BinaryExpr binaryExpr = getBinaryExpressions(compiled).get(0);
        Parameter x = getParameter(compiled, "x");
        Parameter y = getParameter(compiled, "y");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(x, new IntegerRange(-100, 50));
        varState.setVariable(y, new IntegerRange(-10, 20));
        binaryExpr.accept(new AnalysisVisitor("x"), analysisState);
        IntegerRange quotient = (IntegerRange) varState.getVariable(x);
        // TODO: fix variable assignment
        Assertions.assertEquals(-100 / 20, quotient.getMin());
        Assertions.assertEquals(-100 / -10, quotient.getMax());
        Assertions.assertEquals(1, analysisState.getErrorMap().size());
    }

    /**
     * a.min < 0, a.max < 0, b.min < 0, b.max > 0
     *
     * min = a.min / b.max
     * max = a.min / b.min
     */
    @Test
    public void divideZeroInRange3() {
        String code = """
                public class Main {
                    int test(int x, int y) {
                        int x = x / y;
                        return x;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        BinaryExpr binaryExpr = getBinaryExpressions(compiled).get(0);
        Parameter x = getParameter(compiled, "x");
        Parameter y = getParameter(compiled, "y");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(x, new IntegerRange(-100, -50));
        varState.setVariable(y, new IntegerRange(-10, 20));
        binaryExpr.accept(new AnalysisVisitor("x"), analysisState);
        IntegerRange quotient = (IntegerRange) varState.getVariable(x);
        // TODO: fix variable assignment
        Assertions.assertEquals(-100 / 20, quotient.getMin());
        Assertions.assertEquals(-100 / -10, quotient.getMax());
        Assertions.assertEquals(1, analysisState.getErrorMap().size());
    }

    /**
     * a.min > 0, a.max > 0, b.min == 0, b.max > 0
     *
     */
    @Test
    public void divideByZeroInRangeMin() {
        String code = """
                public class Main {
                    int test(int x, int y) {
                        int x = x / y;
                        return x;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        BinaryExpr binaryExpr = getBinaryExpressions(compiled).get(0);
        Parameter x = getParameter(compiled, "x");
        Parameter y = getParameter(compiled, "y");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(x, new IntegerRange(1, 2));
        varState.setVariable(y, new IntegerRange(0, 1));
        binaryExpr.accept(new AnalysisVisitor("x"), analysisState);
        IntegerRange quotient = (IntegerRange) varState.getVariable(x);
        // TODO: fix variable assignment
        Assertions.assertEquals(1 / 1, quotient.getMin());
        Assertions.assertEquals(2 / 1, quotient.getMax());
        Assertions.assertEquals(1, analysisState.getErrorMap().size());
    }

    /**
     * a.min > 0, a.max > 0, b.min < 0, b.max == 0
     *
     */
    @Test
    public void divideByZeroInRangeMax() {
        String code = """
                public class Main {
                    int test(int x, int y) {
                        int x = x / y;
                        return x;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        BinaryExpr binaryExpr = getBinaryExpressions(compiled).get(0);
        Parameter x = getParameter(compiled, "x");
        Parameter y = getParameter(compiled, "y");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(x, new IntegerRange(1, 2));
        varState.setVariable(y, new IntegerRange(-1, 0));
        binaryExpr.accept(new AnalysisVisitor("x"), analysisState);
        IntegerRange quotient = (IntegerRange) varState.getVariable(x);
        // TODO: fix variable assignment
        Assertions.assertEquals(2 / -1, quotient.getMin());
        Assertions.assertEquals(1 / -1, quotient.getMax());
        Assertions.assertEquals(1, analysisState.getErrorMap().size());
    }

    /**
     * a.min > 0, a.max > 0, b.min == 0, b.max == 0
     *
     */
    @Test
    public void divideByZero() {
        String code = """
                public class Main {
                    int test(int x, int y) {
                        int x = x / y;
                        return x;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        BinaryExpr binaryExpr = getBinaryExpressions(compiled).get(0);
        Parameter x = getParameter(compiled, "x");
        Parameter y = getParameter(compiled, "y");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        // TODO: fix casting
        PossibleValues range1 = new IntegerRange(1, 2);
        PossibleValues range2 = new IntegerRange(0, 0);
        varState.setVariable(x, range1);
        varState.setVariable(y, range2);
        binaryExpr.accept(new AnalysisVisitor("x"), analysisState);
        EmptyValue quotient = (EmptyValue) varState.getVariable(x);
        // TODO: fix variable assignment
        Assertions.assertEquals(quotient, new EmptyValue());
        Assertions.assertEquals(1, analysisState.getErrorMap().size());
    }
}
