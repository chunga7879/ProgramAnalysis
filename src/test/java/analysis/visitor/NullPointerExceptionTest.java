package analysis.visitor;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import analysis.values.EmptyValue;
import analysis.values.IntegerRange;
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
                    @Null
                    private Integer x;
                    int test() {
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
    public void nullPointerPotentialExceptionOperatorTest() {
        String code = """
                public class Main {
                    @Min(value=10) @Max(value=20)
                    private Integer x;
                    int test() {
                        int a = x + 1;
                        int b = 1 + x;
                        int c = 40 / x;
                        int d = x / 3;
                        int e = 4 - x;
                        int f = x - 4;
                        int g = 3 * x;
                        int h = x * 3;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(new IntegerRange(11, 21), variablesState.getVariable(getVariable(compiled, "a")));
        Assertions.assertEquals(new IntegerRange(11, 21), variablesState.getVariable(getVariable(compiled, "b")));
        Assertions.assertEquals(new IntegerRange(2, 4), variablesState.getVariable(getVariable(compiled, "c")));
        Assertions.assertEquals(new IntegerRange(3, 6), variablesState.getVariable(getVariable(compiled, "d")));
        Assertions.assertEquals(new IntegerRange(-16, -6), variablesState.getVariable(getVariable(compiled, "e")));
        Assertions.assertEquals(new IntegerRange(6, 16), variablesState.getVariable(getVariable(compiled, "f")));
        Assertions.assertEquals(new IntegerRange(30, 60), variablesState.getVariable(getVariable(compiled, "g")));
        Assertions.assertEquals(new IntegerRange(30, 60), variablesState.getVariable(getVariable(compiled, "h")));
        Assertions.assertEquals(8, analysisState.getErrorMap().size());
    }

    @Test
    public void nullPointerExceptionOperatorAssignmentTest() {
        String code = """
                public class Main {
                    @Null
                    private Integer x;
                    int test() {
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
