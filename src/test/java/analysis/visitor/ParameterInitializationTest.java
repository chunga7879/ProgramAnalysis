package analysis.visitor;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import analysis.values.ArrayValue;
import analysis.values.IntegerRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.Parameter;
import logger.AnalysisLogger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static analysis.visitor.VisitorTestUtils.compile;
import static analysis.visitor.VisitorTestUtils.getParameter;

public class ParameterInitializationTest {
    @BeforeEach
    public void runBefore() {
        AnalysisLogger.setLog(true);
    }

    @Test
    public void ArrayParameterTest() {
        String code = """
                public class Main {
                    void testArrayParameters(int[] a, @NotNull int[] b, @Size(min=10, max=500) int[] c, @NotNull @Size(min=50) int[] d) {
                        System.out.println("hello");
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        Parameter a = getParameter(compiled, "a");
        Parameter b = getParameter(compiled, "b");
        Parameter c = getParameter(compiled, "c");
        Parameter d = getParameter(compiled, "d");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        compiled.accept(new AnalysisVisitor("testArrayParameters"), analysisState);
        ArrayValue aValue = (ArrayValue) varState.getVariable(a);
        ArrayValue bValue = (ArrayValue) varState.getVariable(b);
        ArrayValue cValue = (ArrayValue) varState.getVariable(c);
        ArrayValue dValue = (ArrayValue) varState.getVariable(d);
        Assertions.assertEquals(ArrayValue.DEFAULT_LENGTH, aValue.getLength());
        Assertions.assertTrue(aValue.canBeNull());
        Assertions.assertEquals(ArrayValue.DEFAULT_LENGTH, bValue.getLength());
        Assertions.assertFalse(bValue.canBeNull());
        Assertions.assertEquals(new IntegerRange(10, 500), cValue.getLength());
        Assertions.assertTrue(cValue.canBeNull());
        Assertions.assertEquals(new IntegerRange(50, Integer.MAX_VALUE), dValue.getLength());
        Assertions.assertFalse(dValue.canBeNull());
    }
}
