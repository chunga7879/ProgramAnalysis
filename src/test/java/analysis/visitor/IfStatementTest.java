package analysis.visitor;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import analysis.values.IntegerRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.IfStmt;
import logger.AnalysisLogger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static analysis.visitor.VisitorTestUtils.*;

public class IfStatementTest {
    @BeforeEach
    public void runBefore() {
        AnalysisLogger.setLog(true);
    }

    @Test
    public void ifElseTest() {
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
        IfStmt ifStatement = getIfStatements(compiled).get(0);
        Parameter x = getParameter(compiled, "x");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(x, new IntegerRange(1, 10));
        ifStatement.accept(new AnalysisVisitor(""), analysisState);
        IntegerRange val = (IntegerRange) varState.getVariable(x);
        Assertions.assertEquals(2, val.getMin());
        Assertions.assertEquals(5, val.getMax());
    }

    @Test
    public void ifOnlyTest() {
        String code = """
                public class Main {
                    int test(int x) {
                        if (x >= 5) {
                            x = 4;
                        }
                        return x;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        IfStmt ifStatement = getIfStatements(compiled).get(0);
        Parameter x = getParameter(compiled, "x");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(x, new IntegerRange(-100, 20));
        ifStatement.accept(new AnalysisVisitor(""), analysisState);
        IntegerRange val = (IntegerRange) varState.getVariable(x);
        Assertions.assertEquals(-100, val.getMin());
        Assertions.assertEquals(4, val.getMax());
    }

    @Test
    public void ifElseEmptyTest() {
        String code = """
                public class Main {
                    int test(int x) {
                        if (x != 100) {
                            x = 26000;
                        } else {
                            x = 14;
                        }
                        return x;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        IfStmt ifStatement = getIfStatements(compiled).get(0);
        Parameter x = getParameter(compiled, "x");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(x, new IntegerRange(100, 100));
        ifStatement.accept(new AnalysisVisitor(""), analysisState);
        IntegerRange val = (IntegerRange) varState.getVariable(x);
        Assertions.assertEquals(14, val.getMin());
        Assertions.assertEquals(14, val.getMax());
    }

    @Test
    public void ifElseComplexTest() {
        String code = """
                public class Main {
                    int test(int x, int y) {
                        if ((x > 100 && x <= 110) || y < (205 - 5) && x > 15) {
                            if (true) {
                                x = x + 26000;
                            } else {
                                x = 88888888;
                            }
                        } else if (y == 210 && x != 115 && (x < (y - 200) || x == 2 || x == 4 && y >= 3)) {
                            int c = x;
                            x = y + 108;
                            y = x + 10;
                            if (false || c > 10000) x = 9999999;
                        } else {
                            y = 3;
                            x = 200 - 3;
                        }
                        return x;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        IfStmt ifStatement = getIfStatements(compiled).get(0);
        Parameter x = getParameter(compiled, "x");
        Parameter y = getParameter(compiled, "y");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(x, new IntegerRange(-33, 115));
        varState.setVariable(y, new IntegerRange(-5, 300));
        ifStatement.accept(new AnalysisVisitor(""), analysisState);
        IntegerRange xVal = (IntegerRange) varState.getVariable(x);
        Assertions.assertEquals(197, xVal.getMin());
        Assertions.assertEquals(26115, xVal.getMax());
        IntegerRange yVal = (IntegerRange) varState.getVariable(y);
        Assertions.assertEquals(-5, yVal.getMin());
        Assertions.assertEquals(328, yVal.getMax());
    }

    @Test
    public void ifAndTest() {
        // TODO: need to compute so that each condition in AND is recomputed
        //       until it stops changing
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
        IfStmt ifStatement = getIfStatements(compiled).get(0);
        Parameter x = getParameter(compiled, "x");
        Parameter y = getParameter(compiled, "y");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(x, new IntegerRange(-33, 115));
        varState.setVariable(y, new IntegerRange(-5, 300));
        ifStatement.accept(new AnalysisVisitor(""), analysisState);
        IntegerRange xVal = (IntegerRange) varState.getVariable(x);
        Assertions.assertEquals(2, xVal.getMin());
        Assertions.assertEquals(52, xVal.getMax());
        IntegerRange yVal = (IntegerRange) varState.getVariable(y);
        Assertions.assertEquals(-5, yVal.getMin());
        Assertions.assertEquals(300, yVal.getMax());
    }
}
