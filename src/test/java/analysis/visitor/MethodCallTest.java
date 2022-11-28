package analysis.visitor;

import analysis.model.AnalysisError;
import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import analysis.values.NullValue;
import analysis.values.StringValue;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.VariableDeclarator;
import logger.AnalysisLogger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static analysis.visitor.VisitorTestUtils.*;

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
        NullValue val = (NullValue) variablesState.getVariable(s);
        Assertions.assertEquals(NullValue.VALUE, val);
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
    // endregion ---- annotation tests
}