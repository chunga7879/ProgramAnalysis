package analysis.visitor;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import analysis.values.EmptyValue;
import com.github.javaparser.ast.CompilationUnit;
import logger.AnalysisLogger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static analysis.visitor.VisitorTestUtils.*;

public class NullPointerExceptionTest {
    private VariablesState variablesState;
    private AnalysisState analysisState;

    @BeforeEach
    public void runBefore() {
        variablesState = new NonEmptyVariablesState();
        analysisState = new AnalysisState(variablesState);
        AnalysisLogger.setLog(false);
    }

    @Test
    public void nullPointerExceptionOperatorTest() {
        String code = """
                public class Main {
                    int test() {
                        Integer x = null;
                        int a = x + 1;
                        int b = 1 + x;
                        int c = 1 / x;
                        int d = x / 1;
                        int e = 1 - x;
                        int f = x - 1;
                        int g = 1 * x;
                        int h = x * 1;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(new EmptyValue(), variablesState.getVariable(getVariable(compiled, "a")));
        Assertions.assertEquals(new EmptyValue(), variablesState.getVariable(getVariable(compiled, "b")));
        Assertions.assertEquals(new EmptyValue(), variablesState.getVariable(getVariable(compiled, "c")));
        Assertions.assertEquals(new EmptyValue(), variablesState.getVariable(getVariable(compiled, "d")));
        Assertions.assertEquals(new EmptyValue(), variablesState.getVariable(getVariable(compiled, "e")));
        Assertions.assertEquals(new EmptyValue(), variablesState.getVariable(getVariable(compiled, "f")));
        Assertions.assertEquals(new EmptyValue(), variablesState.getVariable(getVariable(compiled, "g")));
        Assertions.assertEquals(new EmptyValue(), variablesState.getVariable(getVariable(compiled, "h")));
        Assertions.assertEquals(8, analysisState.getErrorMap().size());
    }

    @Test
    public void nullPointerExceptionOperatorAssignmentTest() {
        String code = """
                public class Main {
                    int test() {
                        Integer x = null;
                        int a = 1;
                        a += x;
                        int b = 1;
                        b -= x;
                        int c = 1;
                        c /= x;
                        int d = 1;
                        d *= x;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(new EmptyValue(), variablesState.getVariable(getVariable(compiled, "a")));
        Assertions.assertEquals(new EmptyValue(), variablesState.getVariable(getVariable(compiled, "b")));
        Assertions.assertEquals(new EmptyValue(), variablesState.getVariable(getVariable(compiled, "c")));
        Assertions.assertEquals(new EmptyValue(), variablesState.getVariable(getVariable(compiled, "d")));
        Assertions.assertEquals(4, analysisState.getErrorMap().size());
    }
}
