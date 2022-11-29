package analysis.visitor;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static analysis.visitor.VisitorTestUtils.*;

public class ObjectCreationExprTest {
    private VariablesState variablesState;
    private AnalysisState analysisState;

    @BeforeEach
    public void runBefore() {
        variablesState = new NonEmptyVariablesState();
        analysisState = new AnalysisState(variablesState);
    }

    // TODO: implement
    @Test
    public void objectCreationTest() {
        String code = """
                public class Main {
                    int test() {
                        Integer a = new Integer(1);
                        String s = "";
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }
}
