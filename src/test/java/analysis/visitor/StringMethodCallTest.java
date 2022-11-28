package analysis.visitor;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import analysis.values.BooleanValue;
import analysis.values.IntegerRange;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static analysis.visitor.VisitorTestUtils.*;

public class StringMethodCallTest {
    private VariablesState variablesState;
    private AnalysisState analysisState;

    @BeforeEach
    public void runBefore() {
        variablesState = new NonEmptyVariablesState();
        analysisState = new AnalysisState(variablesState);
    }

    @Test
    public void stringLengthTest() {
        String code = """
                public class Main {
                    void test(String c) {
                        String a = "";
                        String b = "str";
                        int x = a.length();
                        int y = b.length();
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        IntegerRange x = (IntegerRange) variablesState.getVariable(getVariable(compiled, "x"));
        IntegerRange y = (IntegerRange) variablesState.getVariable(getVariable(compiled, "y"));
        Assertions.assertEquals(0, x.getMin());
        Assertions.assertEquals(0, x.getMax());
        Assertions.assertEquals(3, y.getMax());
        Assertions.assertEquals(3, y.getMax());
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void stringIsEmptyTest() {
        String code = """
                public class Main {
                    void test(String c) {
                        String a = "";
                        String b = "str";
                        boolean x = a.isEmpty();
                        boolean y = b.isEmpty();
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        BooleanValue x = (BooleanValue) variablesState.getVariable(getVariable(compiled, "x"));
        BooleanValue y = (BooleanValue) variablesState.getVariable(getVariable(compiled, "y"));
        Assertions.assertTrue(x.canBeTrue());
        Assertions.assertTrue(x.canBeFalse());
        Assertions.assertFalse(x.canBeTrue());
        Assertions.assertFalse(x.canBeFalse());
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }
}
