package analysis.visitor;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import com.github.javaparser.ast.CompilationUnit;
import logger.AnalysisLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static analysis.visitor.VisitorTestUtils.compile;

public class MethodCallTest {
    private AnalysisState analysisState;

    @BeforeEach
    public void runBefore() {
        analysisState = new AnalysisState(new VariablesState());
        AnalysisLogger.setLog(true);
    }

    @Test
    public void nullPointerExceptionTest() {
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
    }

    @Test
    public void throwsJavaDocTest() {
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
    }
    // endregion ---- annotation tests
}
