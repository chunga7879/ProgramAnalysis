package analysis.visitor;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import analysis.values.ExtendableObjectValue;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import logger.AnalysisLogger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static analysis.visitor.VisitorTestUtils.*;

public class ObjectTest {
    private VariablesState variablesState;
    private AnalysisState analysisState;

    @BeforeEach
    public void runBefore() {
        variablesState = new NonEmptyVariablesState();
        analysisState = new AnalysisState(variablesState);
        AnalysisLogger.setLog(false);
    }

    @Test
    public void objectCreationTest() {
        String code = """
                public class Main {
                    int test() {
                        Object a = new Object();
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        VariableDeclarator a = getVariable(compiled, "a");
        ExtendableObjectValue val = (ExtendableObjectValue) variablesState.getVariable(a);
        Assertions.assertTrue(val.canBeNull());
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void objectParameterTest() {
        String code = """
                public class Main {
                    int test(@NotNull Object a, Object b) {
                        // ...
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Parameter a = getParameter(compiled, "a");
        Parameter b = getParameter(compiled, "b");
        ExtendableObjectValue valA = (ExtendableObjectValue) variablesState.getVariable(a);
        ExtendableObjectValue valB = (ExtendableObjectValue) variablesState.getVariable(b);
        Assertions.assertFalse(valA.canBeNull());
        Assertions.assertTrue(valB.canBeNull());
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }
}
