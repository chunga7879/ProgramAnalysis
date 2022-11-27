package analysis.visitor;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import analysis.values.AnyValue;
import analysis.values.IntegerRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.Parameter;
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
        Assertions.assertEquals(new AnyValue(), varState.getVariable(b));
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
}
