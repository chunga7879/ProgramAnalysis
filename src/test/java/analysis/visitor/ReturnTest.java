package analysis.visitor;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import com.github.javaparser.ast.CompilationUnit;
import logger.AnalysisLogger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static analysis.visitor.VisitorTestUtils.compile;

public class ReturnTest {
    private VariablesState variablesState;
    private AnalysisState analysisState;

    @BeforeEach
    public void runBefore() {
        variablesState = new VariablesState();
        analysisState = new AnalysisState(variablesState);
        AnalysisLogger.setLog(false);
    }

    @Test
    public void returnPositiveAnnotationWithoutError() {
        String code = """
                public class Main {
                    @Positive
                    int test() {
                        if (false) return -100;
                        return 1;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void returnSizeAnnotationWithoutError() {
        String code = """
                public class Main {
                    @Size(min = 10, max = 200)
                    int[] test(int a, int b) {
                        if (b < 2) return null;
                        return a > 0 ? new int[10] : new int[40];
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void returnSizeAnnotationWithError() {
        String code = """
                public class Main {
                    @NotNull @Size(min = 10, max = 200)
                    int[] test(int a, int b) {
                        if (b < 2) return null;
                        return a > 0 ? new int[9] : new int[40];
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(2, analysisState.getErrorMap().size());
    }
}
