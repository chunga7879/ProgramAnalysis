package analysis.visitor;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import analysis.values.AnyValue;
import analysis.values.BooleanValue;
import analysis.values.IntegerRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.stmt.WhileStmt;
import logger.AnalysisLogger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static analysis.visitor.VisitorTestUtils.*;

public class WhileStatementTest {
    @BeforeEach
    public void runBefore() {
        AnalysisLogger.setLog(false);
    }

    @Test
    public void whileFixedTest() {
        String code = """
                public class Main {
                    void main(int a, int b) {
                        while (a < 30 || b < 50) {
                            a = a + 2;
                            if (a >= 30) b = b + 5;
                        }
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        WhileStmt whileStatement = getWhileStatements(compiled).get(0);
        Parameter a = getParameter(compiled, "a");
        Parameter b = getParameter(compiled, "b");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(a, new IntegerRange(5, 10));
        varState.setVariable(b, new IntegerRange(2, 5));
        whileStatement.accept(new AnalysisVisitor(""), analysisState);
        Assertions.assertEquals(new IntegerRange(41, 54), varState.getVariable(a));
        Assertions.assertEquals(new IntegerRange(50, 54), varState.getVariable(b));
    }

    @Test
    public void whileAnyTest() {
        String code = """
                public class Main {
                    void main(int a, int b) {
                        while (a < 30 || b < 50) {
                            a = a + 2;
                            if (a >= 30) b = b + 5;
                        }
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        WhileStmt whileStatement = getWhileStatements(compiled).get(0);
        Parameter a = getParameter(compiled, "a");
        Parameter b = getParameter(compiled, "b");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(a, new IntegerRange(5, 10));
        varState.setVariable(b, new AnyValue());
        whileStatement.accept(new AnalysisVisitor(""), analysisState);
        Assertions.assertEquals(new IntegerRange(30, Integer.MAX_VALUE), varState.getVariable(a));
        Assertions.assertEquals(new IntegerRange(50, Integer.MAX_VALUE), varState.getVariable(b));
    }

    @Test
    public void whileInfiniteTest() {
        String code = """
                public class Main {
                    void main() {
                        while (true || false || true) {
                        }
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        WhileStmt whileStatement = getWhileStatements(compiled).get(0);
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        whileStatement.accept(new AnalysisVisitor(""), analysisState);
        Assertions.assertTrue(varState.isDomainEmpty());
    }

    @Test
    public void whileBreakTest() {
        String code = """
                public class Main {
                    void test() {
                        int i = 10;
                        while (true) {
                            if (i > 20) {
                                break;
                            }
                            i++;
                        }
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        VariablesState varState = new VariablesState();
        VariableDeclarator i = getVariable(compiled, "i");
        AnalysisState analysisState = new AnalysisState(varState);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(new IntegerRange(21), varState.getVariable(i));
    }

    @Test
    public void whileContinueTest() {
        String code = """
                public class Main {
                    void test() {
                        int i = 0;
                        int a = 1;
                        while (a <= 10) {
                            i++;
                            if (true) {
                              a++;
                              continue;
                            }
                            a = 100;
                        }
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        VariablesState varState = new VariablesState();
        VariableDeclarator i = getVariable(compiled, "i");
        VariableDeclarator a = getVariable(compiled, "a");
        AnalysisState analysisState = new AnalysisState(varState);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(new IntegerRange(10), varState.getVariable(i));
        Assertions.assertEquals(new IntegerRange(11), varState.getVariable(a));
    }

    @Test
    public void whileCoverDomainTest() {
        String code = """
                public class Main {
                    void test() {
                        boolean a = true;
                        boolean b = true;
                        int x = 0;
                        while(true) {
                            if (a && b) {
                                a = false;
                                b = false;
                            } else if (!a && !b) {
                                a = true;
                                b = false;
                            } else if (a && !b) {
                                a = false;
                                b = true;
                            } else {
                                x = 10;
                                break;
                            }
                        }
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        VariableDeclarator x = getVariable(compiled, "x");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertFalse(varState.isDomainEmpty());
        Assertions.assertEquals(new IntegerRange(10), varState.getVariable(x));
    }

    @Test
    public void whileSwitchDirectionTest() {
        String code = """
                public class Main {
                    void test() {
                        boolean up = true;
                        int sum = 0;
                        int i = 0;
                        while (i <= 100 && i >= -100) {
                            sum++;
                
                            if (up) i++;
                            else i--;
                
                            if (i == 100) up = false;
                            else if (i == -100) up = true;
                
                            if (sum == 210) break;
                        }
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        VariableDeclarator i = getVariable(compiled, "i");
        VariableDeclarator up = getVariable(compiled, "up");
        VariableDeclarator sum = getVariable(compiled, "sum");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertFalse(varState.isDomainEmpty());
        Assertions.assertEquals(new IntegerRange(-10), varState.getVariable(i));
        Assertions.assertEquals(new IntegerRange(210), varState.getVariable(sum));
        Assertions.assertEquals(BooleanValue.FALSE, varState.getVariable(up));
    }
}
