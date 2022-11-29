package analysis.visitor;

import analysis.model.AnalysisError;
import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import analysis.values.*;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.VariableDeclarator;
import logger.AnalysisLogger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static analysis.visitor.VisitorTestUtils.compile;
import static analysis.visitor.VisitorTestUtils.getVariable;

public class MethodCallTest {
    private VariablesState variablesState;
    private AnalysisState analysisState;

    @BeforeEach
    public void runBefore() {
        variablesState = new VariablesState();
        analysisState = new AnalysisState(variablesState);
        AnalysisLogger.setLog(false);
    }

    @Test
    public void nullPointerExceptionTestWithError() {
        String code = """
                public class Main {
                    void test() {
                        String s = null;
                        int x = s.length();
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        VariableDeclarator s = getVariable(compiled, "s");
        VariableDeclarator x = getVariable(compiled, "x");
        Assertions.assertEquals(EmptyValue.VALUE, variablesState.getVariable(s));
        Assertions.assertEquals(EmptyValue.VALUE, variablesState.getVariable(x));
        Assertions.assertEquals(1, analysisState.getErrorMap().size());
    }

    @Test
    public void nullPointerExceptionPossibleTestWithError() {
        String code = """
                public class Main {
                    void test(int a) {
                        String s;
                        if (a > 0) s = null;
                        else s = "hi";
                        int x = s.length();
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        VariableDeclarator s = getVariable(compiled, "s");
        VariableDeclarator x = getVariable(compiled, "x");
        Assertions.assertEquals(new StringValue(2, 2, false), variablesState.getVariable(s));
        Assertions.assertEquals(new IntegerRange(2), variablesState.getVariable(x));
        Assertions.assertEquals(1, analysisState.getErrorMap().size());
    }

    @Test
    public void nullPointerExceptionTestWithoutError1() {
        String code = """
                public class Main {
                    void test() {
                        String s = "str";
                        int x = s.length();
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        VariableDeclarator s = getVariable(compiled, "s");
        StringValue val = (StringValue) variablesState.getVariable(s);
        Assertions.assertEquals(new StringValue("str"), val);
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void nullPointerExceptionTestWithoutError2() {
        String code = """
                public class Main {
                    void test() {
                        Integer i = new Integer(1);
                        int x = i.intValue();
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void nullPointerExceptionTestWithPossibleError() {
        String code = """
                public class Main {
                    void test(String s) {
                        int x = s.length();
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(1, analysisState.getErrorMap().size());
    }

    @Test
    public void throwsRuntimeExceptionTest() {
        String code = """
                public class Main {
                    /**
                     * @throws RuntimeException
                     */
                    void foo() throws RuntimeException {
                        // ...
                    }
                    
                    void test() {
                        foo();
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(1, analysisState.getErrorMap().size());
    }

    @Test
    public void throwsNullPointerExceptionTest() {
        String code = """
                public class Main {
                    /**
                     * @throws NullPointerException
                     */
                    void foo() throws NullPointerException {
                        // ...
                    }
                    
                    void test() {
                        foo();
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(1, analysisState.getErrorMap().size());
    }

    @Test
    public void throwsMultipleExceptionsTest() {
        String code = """
                public class Main {
                    /**
                     * @throws NullPointerException
                     * @throws ArithmeticException
                     */
                    void foo() throws NullPointerException, ArithmeticException {
                        // ...
                    }
                    
                    void test() {
                        foo();
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        for (Set<AnalysisError> errors: analysisState.getErrorMap().values()) {
            Assertions.assertEquals(2, errors.size());
        }
    }

    @Test
    public void throwsNonRuntimeExceptionTest() {
        String code = """
                public class Main {
                    /**
                     * @throws Exception
                     */
                    void foo() throws Exception {
                        // ...
                    }
                    
                    void test() {
                        foo();
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void throwsExceptionWithoutJavadocsTest() {
        String code = """
                public class Main {
                    void foo() throws NullPointerException {
                        // ...
                    }
                    
                    void test() {
                        foo();
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(1, analysisState.getErrorMap().size());
    }

    // region ---- annotation tests
    @Test
    public void notNullAnnotationTestWithError() {
        String code = """
                public class Main {
                    void foo(@NotNull String bar) {
                        // ...
                    }
                                
                    void test() {
                        foo(null);
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(1, analysisState.getErrorMap().size());
    }

    @Test
    public void notNullAnnotationTestWithPossibleError() {
        String code = """
                public class Main {
                    void foo(@NotNull String bar) {
                        // ...
                    }
                                
                    void test(String s) {
                        foo(s);
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(1, analysisState.getErrorMap().size());
    }

    @Test
    public void notNullAnnotationTestWithoutError() {
        String code = """
                public class Main {
                    void foo(@NotNull String bar) {
                        // ...
                    }
                                
                    void test() {
                        foo("str");
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void positiveAnnotationTestWithError() {
        String code = """
                public class Main {
                    void foo(@Positive int bar) {
                        // ...
                    }
                                
                    void test() {
                        foo(0);
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(1, analysisState.getErrorMap().size());
    }

    @Test
    public void positiveAnnotationTestWithoutError() {
        String code = """
                public class Main {
                    void foo(@Positive int bar) {
                        // ...
                    }
                                
                    void test() {
                        foo(1);
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void positiveOrZeroAnnotationTestWithError() {
        String code = """
                public class Main {
                    void foo(@PositiveOrZero int bar) {
                        // ...
                    }
                                
                    void test() {
                        foo(-1);
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(1, analysisState.getErrorMap().size());
    }

    @Test
    public void positiveOrZeroAnnotationTestWithoutError() {
        String code = """
                public class Main {
                    void foo(@PositiveOrZero int bar) {
                        // ...
                    }
                                
                    void test() {
                        foo(0);
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void negativeAnnotationTestWithError() {
        String code = """
                public class Main {
                    void foo(@Negative int bar) {
                        // ...
                    }
                                
                    void test() {
                        foo(0);
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(1, analysisState.getErrorMap().size());
    }

    @Test
    public void negativeAnnotationTestWithoutError() {
        String code = """
                public class Main {
                    void foo(@Negative int bar) {
                        // ...
                    }
                                
                    void test() {
                        foo(-1);
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void negativeOrZeroAnnotationTestWithError() {
        String code = """
                public class Main {
                    void foo(@NegativeOrZero int bar) {
                        // ...
                    }
                                
                    void test() {
                        foo(1);
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(1, analysisState.getErrorMap().size());
    }


    @Test
    public void negativeOrZeroAnnotationTestWithoutError() {
        String code = """
                public class Main {
                    void foo(@NegativeOrZero int bar) {
                        // ...
                    }
                                
                    void test() {
                        foo(0);
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void notEmptyWithoutError() {
        String code = """
                public class Main {
                    void foo(@NotEmpty String bar, @NotEmpty char[] bar2) {
                        // ...
                    }
                                
                    void test() {
                        foo("hello", new char[4]);
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void notEmptyWithError() {
        String code = """
                public class Main {
                    void foo(@NotEmpty String bar, @NotEmpty char[] bar2) {
                        // ...
                    }
                                
                    void test(int i, String a) {
                        String b = i > 0 ? null : "hi";
                        foo(a, null);
                        foo(b, new char[0]);
                        foo(null, i > 0 ? new char[10] : null);
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(3, analysisState.getErrorMap().size());
    }

    @Test
    public void minMaxWithoutError() {
        String code = """
                public class Main {
                    void foo(@Min(value = 10) @Min(value = -20) @Max(value = 100) int bar) {
                        // ...
                    }
                                
                    void test(int a) {
                        int b = a > 0 ? 10 : 100;
                        foo(b);
                        foo(100);
                        foo(a > 0 ? 17 : 21);
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void minMaxWithError() {
        String code = """
                public class Main {
                    void foo(@Min(value = 10) @Max(value = 100) int bar) {
                        // ...
                    }
                                
                    void test(int a) {
                        int b = a > 0 ? -10 : 10;
                        foo(b);
                        foo(101);
                        foo(a > 0 ? -9 : 101);
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(3, analysisState.getErrorMap().size());
    }

    @Test
    public void sizeWithoutError() {
        String code = """
                public class Main {
                    void foo(@Size(max = 20, min = 8) int[] bar, @Size(min = 1, max = 4) String[] bar2) {
                        // ...
                    }
                                
                    void test(int a) {
                        foo(a > 0 ? new int[8] : new int[20], new String[3]);
                        foo(new int[12], new String[1]);
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void sizeWithError() {
        String code = """
                public class Main {
                    void foo(@Size(max = 20, min = 8) int[] bar, @Size(min = 1, max = 4) String[] bar2) {
                        // ...
                    }
                                
                    void test(int a) {
                        foo(a > 0 ? new int[0] : new int[21], new String[4]);
                        foo(new int[21], new String[0]);
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(2, analysisState.getErrorMap().size());
    }

    @Test
    public void methodCallReturnTypeTest() {
        String code = """
                public class Main {
                    @Positive
                    int intMethod() {
                        return 1;
                    }
                    
                    @NegativeOrZero @NotNull
                    Integer integerMethod() {
                        return 0;
                    }
                    
                    @NotEmpty
                    String stringMethod() {
                        return "";
                    }
                                
                    void test() {
                        int a = intMethod();
                        Integer b = integerMethod();
                        String c = stringMethod();
                        char d = c.charAt(0);
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        VariableDeclarator a = getVariable(compiled, "a");
        VariableDeclarator b = getVariable(compiled, "b");
        VariableDeclarator c = getVariable(compiled, "c");
        VariableDeclarator d = getVariable(compiled, "d");
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(new IntegerRange(1, Integer.MAX_VALUE), variablesState.getVariable(a));
        Assertions.assertEquals(BoxedPrimitive.create(new IntegerRange(Integer.MIN_VALUE, 0), false), variablesState.getVariable(b));
        Assertions.assertEquals(new StringValue(1, Integer.MAX_VALUE), variablesState.getVariable(c));
        Assertions.assertEquals(CharValue.ANY_VALUE, variablesState.getVariable(d));
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void noNullPointerOnStaticCall() {
        String code = """
                public class Main {
                    public static void foo() {}                                
                    void test() {
                        Main.foo();
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }
    // endregion ---- annotation tests
}
