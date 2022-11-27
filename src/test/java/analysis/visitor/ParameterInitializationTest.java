package analysis.visitor;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import analysis.values.ArrayValue;
import analysis.values.IntegerRange;
import analysis.values.NullValue;
import analysis.values.StringValue;
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
    public void integerParameterTest() {
        String code = """
                public class Main {
                    void test(
                                        int a,
                        @Negative       int b,
                        @NegativeOrZero int c,
                        @Positive       int d,
                        @PositiveOrZero int e,
                        @PositiveOrZero @NegativeOrZero int f
                    ) {
                        System.out.println("hello");
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        Parameter a = getParameter(compiled, "a");
        Parameter b = getParameter(compiled, "b");
        Parameter c = getParameter(compiled, "c");
        Parameter d = getParameter(compiled, "d");
        Parameter e = getParameter(compiled, "e");
        Parameter f = getParameter(compiled, "f");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(IntegerRange.ANY_VALUE, varState.getVariable(a));
        Assertions.assertEquals(new IntegerRange(Integer.MIN_VALUE, -1), varState.getVariable(b));
        Assertions.assertEquals(new IntegerRange(Integer.MIN_VALUE, 0), varState.getVariable(c));
        Assertions.assertEquals(new IntegerRange(1, Integer.MAX_VALUE), varState.getVariable(d));
        Assertions.assertEquals(new IntegerRange(0, Integer.MAX_VALUE), varState.getVariable(e));
        Assertions.assertEquals(new IntegerRange(0), varState.getVariable(f));
    }

    @Test
    public void integerMinMaxParameterTest() {
        String code = """
                public class Main {
                    void test(
                        int a,
                        @Min(value = -1) int b,
                        @Max(value = 10) int c,
                        @Min(value = -100) @Max(value = 219) int d,
                        @Min(value = -10) @Min(value = 33) int e
                    ) {
                        System.out.println("hello");
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        Parameter a = getParameter(compiled, "a");
        Parameter b = getParameter(compiled, "b");
        Parameter c = getParameter(compiled, "c");
        Parameter d = getParameter(compiled, "d");
        Parameter e = getParameter(compiled, "e");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(IntegerRange.ANY_VALUE, varState.getVariable(a));
        Assertions.assertEquals(new IntegerRange(-1, Integer.MAX_VALUE), varState.getVariable(b));
        Assertions.assertEquals(new IntegerRange(Integer.MIN_VALUE, 10), varState.getVariable(c));
        Assertions.assertEquals(new IntegerRange(-100, 219), varState.getVariable(d));
        Assertions.assertEquals(new IntegerRange(33, Integer.MAX_VALUE), varState.getVariable(e));
    }

    @Test
    public void stringParameterTest() {
        String code = """
                public class Main {
                    void test(
                        String a,
                        @NotNull String b,
                        @Null String c,
                        @NotEmpty String d
                    ) {
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
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(StringValue.ANY_VALUE, varState.getVariable(a));
        Assertions.assertEquals(StringValue.ANY_VALUE.withNotNullable(), varState.getVariable(b));
        Assertions.assertEquals(NullValue.VALUE, varState.getVariable(c));
        Assertions.assertEquals(new StringValue(1, Integer.MAX_VALUE).withNotNullable(), varState.getVariable(d));
    }

    @Test
    public void arrayParameterTest() {
        String code = """
                public class Main {
                    void testArrayParameters(
                        int[] a,
                        @NotNull int[] b,
                        @Size(min=10, max=500) int[] c,
                        @NotNull @Size(min=50) int[] d,
                        @NotEmpty String[] e,
                        @Null char[] f
                    ) {
                        System.out.println("hello");
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        Parameter a = getParameter(compiled, "a");
        Parameter b = getParameter(compiled, "b");
        Parameter c = getParameter(compiled, "c");
        Parameter d = getParameter(compiled, "d");
        Parameter e = getParameter(compiled, "e");
        Parameter f = getParameter(compiled, "f");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        compiled.accept(new AnalysisVisitor("testArrayParameters"), analysisState);
        ArrayValue aValue = (ArrayValue) varState.getVariable(a);
        ArrayValue bValue = (ArrayValue) varState.getVariable(b);
        ArrayValue cValue = (ArrayValue) varState.getVariable(c);
        ArrayValue dValue = (ArrayValue) varState.getVariable(d);
        ArrayValue eValue = (ArrayValue) varState.getVariable(e);
        Assertions.assertEquals(ArrayValue.DEFAULT_LENGTH, aValue.getLength());
        Assertions.assertTrue(aValue.canBeNull());
        Assertions.assertEquals(ArrayValue.DEFAULT_LENGTH, bValue.getLength());
        Assertions.assertFalse(bValue.canBeNull());
        Assertions.assertEquals(new IntegerRange(10, 500), cValue.getLength());
        Assertions.assertTrue(cValue.canBeNull());
        Assertions.assertEquals(new IntegerRange(50, Integer.MAX_VALUE), dValue.getLength());
        Assertions.assertFalse(dValue.canBeNull());
        Assertions.assertEquals(new IntegerRange(1, Integer.MAX_VALUE), eValue.getLength());
        Assertions.assertFalse(eValue.canBeNull());
        Assertions.assertEquals(NullValue.VALUE, varState.getVariable(f));
    }
}
