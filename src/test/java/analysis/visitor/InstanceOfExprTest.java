package analysis.visitor;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import analysis.values.BooleanValue;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.VariableDeclarator;
import logger.AnalysisLogger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static analysis.visitor.VisitorTestUtils.*;

public class InstanceOfExprTest {
    private VariablesState variablesState;
    private AnalysisState analysisState;

    @BeforeEach
    public void runBefore() {
        variablesState = new NonEmptyVariablesState();
        analysisState = new AnalysisState(variablesState);
        AnalysisLogger.setLog(false);
    }

    @Test
    public void validInstanceOfStringTest() {
        String code = """
                public class Main {
                    void test() {
                        String s = "str";
                        boolean b = s instanceof String;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        VariableDeclarator s = getVariable(compiled, "b");
        BooleanValue value = (BooleanValue) variablesState.getVariable(s);
        Assertions.assertTrue(value.canBeTrue());
        Assertions.assertFalse(value.canBeFalse());
    }

    @Test
    public void validInstanceOfIntegerTest() {
        String code = """
                public class Main {
                    void test() {
                        Integer i = new Integer(5);
                        boolean b = i instanceof Integer;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        VariableDeclarator s = getVariable(compiled, "b");
        BooleanValue value = (BooleanValue) variablesState.getVariable(s);
        Assertions.assertTrue(value.canBeTrue());
        Assertions.assertFalse(value.canBeFalse());
    }

    @Test
    public void invalidInstanceOfTest() {
        String code = """
                public class Main {
                    void test() {
                        Integer i = new Integer(3);
                        boolean b = i instanceof String;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        VariableDeclarator s = getVariable(compiled, "b");
        BooleanValue value = (BooleanValue) variablesState.getVariable(s);
        Assertions.assertFalse(value.canBeTrue());
        Assertions.assertTrue(value.canBeFalse());
    }
}
