package analysis.visitor;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import analysis.values.BooleanValue;
import analysis.values.IntegerRange;
import analysis.values.StringValue;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.BlockStmt;
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
                        int z = c.length();
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        BlockStmt block = getBlockStatements(compiled).get(0);
        Parameter c = getParameter(compiled, "c");
        variablesState.setVariable(c, new StringValue(0, 10, false));
        block.accept(new AnalysisVisitor("test"), analysisState);
        IntegerRange x = (IntegerRange) variablesState.getVariable(getVariable(compiled, "x"));
        IntegerRange y = (IntegerRange) variablesState.getVariable(getVariable(compiled, "y"));
        IntegerRange z = (IntegerRange) variablesState.getVariable(getVariable(compiled, "z"));
        Assertions.assertEquals(0, x.getMin());
        Assertions.assertEquals(0, x.getMax());
        Assertions.assertEquals(3, y.getMax());
        Assertions.assertEquals(3, y.getMax());
        Assertions.assertEquals(0, z.getMin());
        Assertions.assertEquals(10, z.getMax());
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
                        boolean z = c.isEmpty();
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        BlockStmt block = getBlockStatements(compiled).get(0);
        Parameter c = getParameter(compiled, "c");
        variablesState.setVariable(c, new StringValue(0, 10, false));
        block.accept(new AnalysisVisitor("test"), analysisState);
        BooleanValue x = (BooleanValue) variablesState.getVariable(getVariable(compiled, "x"));
        BooleanValue y = (BooleanValue) variablesState.getVariable(getVariable(compiled, "y"));
        BooleanValue z = (BooleanValue) variablesState.getVariable(getVariable(compiled, "z"));
        Assertions.assertTrue(x.canBeTrue());
        Assertions.assertFalse(x.canBeFalse());
        Assertions.assertFalse(y.canBeTrue());
        Assertions.assertTrue(y.canBeFalse());
        Assertions.assertTrue(z.canBeTrue());
        Assertions.assertTrue(z.canBeFalse());
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }
}
