package analysis.visitor;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import com.github.javaparser.ast.CompilationUnit;
import logger.AnalysisLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static analysis.visitor.VisitorTestUtils.compile;

public class CastExprTest {

    @BeforeEach
    public void runBefore() {
        AnalysisLogger.setLog(false);
    }

    @Test
    public void invalidCastTest() {
        String code = """
                public class Main {
                    void test() {
                        Integer i = new Integer(10);
                        String s = (String) i;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
    }

    @Test
    public void validCastTest() {
        String code = """
                public class Main {
                    void test() {
                        int i = 5;
                        long l = (long) i;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
    }
}
