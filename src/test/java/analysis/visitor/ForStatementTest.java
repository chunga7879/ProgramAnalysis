package analysis.visitor;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import analysis.values.IntegerRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.stmt.ForStmt;
import logger.AnalysisLogger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static analysis.visitor.VisitorTestUtils.*;

public class ForStatementTest {
    @BeforeEach
    public void runBefore() {
        AnalysisLogger.setLog(true);
    }

    @Test
    public void forLoopFixedTest() {
        String code = """
                public class Main {
                    void testForLoopFixed(int a, int b) {
                        for (int i = 0; i < b; i = i + 1) {
                            a = a + i;
                        }
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        ForStmt forStatement = getForStatements(compiled).get(0);
        Parameter a = getParameter(compiled, "a");
        Parameter b = getParameter(compiled, "b");
        VariableDeclarator i = getVariable(compiled, "i");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(a, new IntegerRange(10, 10));
        varState.setVariable(b, new IntegerRange(10, 20));
        forStatement.accept(new AnalysisVisitor(""), analysisState);
        IntegerRange aVal = (IntegerRange) varState.getVariable(a);
        Assertions.assertEquals(55, aVal.getMin());
        Assertions.assertEquals(200, aVal.getMax());
        IntegerRange iVal = (IntegerRange) varState.getVariable(i);
        Assertions.assertEquals(10, iVal.getMin());
        Assertions.assertEquals(20, iVal.getMax());
    }

    @Test
    public void forLoopAnyTest() {
        String code = """
                public class Main {
                    void testForLoopFixed(int a, int b) {
                        for (int i = 0; i < b; i = i + 1) {
                            a = a + i;
                        }
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        ForStmt forStatement = getForStatements(compiled).get(0);
        Parameter a = getParameter(compiled, "a");
        Parameter b = getParameter(compiled, "b");
        VariableDeclarator i = getVariable(compiled, "i");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(a, new IntegerRange(10, 10));
        varState.setVariable(b, new IntegerRange(Integer.MIN_VALUE, Integer.MAX_VALUE));
        forStatement.accept(new AnalysisVisitor(""), analysisState);
        IntegerRange aVal = (IntegerRange) varState.getVariable(a);
        Assertions.assertEquals(10, aVal.getMin());
        Assertions.assertEquals(Integer.MAX_VALUE, aVal.getMax());
        IntegerRange iVal = (IntegerRange) varState.getVariable(i);
        Assertions.assertEquals(0, iVal.getMin());
        Assertions.assertEquals(Integer.MAX_VALUE, iVal.getMax());
    }

    @Test
    public void forLoopInfiniteTest() {
        String code = """
                public class Main {
                    void testForLoopFixed(int a) {
                        for (int i = 0; ; i = i + 1) {
                            a = a + i;
                        }
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        ForStmt forStatement = getForStatements(compiled).get(0);
        Parameter a = getParameter(compiled, "a");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(a, new IntegerRange(10, 10));
        forStatement.accept(new AnalysisVisitor(""), analysisState);
        Assertions.assertTrue(varState.isDomainEmpty());
    }
}
