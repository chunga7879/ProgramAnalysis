package analysis.visitor;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import analysis.values.BooleanValue;
import analysis.values.BoxedPrimitive;
import analysis.values.IntegerRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.stmt.BlockStmt;
import logger.AnalysisLogger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static analysis.visitor.VisitorTestUtils.*;

public class BooleanOperatorTest {
    @BeforeEach
    public void runBefore() {
        AnalysisLogger.setLog(true);
    }

    @Test
    public void booleanOperatorsTest() {
        String code = """
                public class Main {
                    void test(int a) {
                        int x;
                        if (a > 0) x = 5;
                        else x = 10;
                        int y = -4;
                        int z = 2;
                        boolean bool = ++x > 10 && ((-z > 5) || (y = 10) == 10);
                        boolean bool2 = (z = 3) < 5;
                        boolean bool3 = !bool2;
                        if (!bool2) throw new RuntimeException();
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        Parameter a = getParameter(compiled, "a");
        VariableDeclarator x = getVariable(compiled, "x");
        VariableDeclarator y = getVariable(compiled, "y");
        VariableDeclarator z = getVariable(compiled, "z");
        VariableDeclarator bool = getVariable(compiled, "bool");
        VariableDeclarator bool2 = getVariable(compiled, "bool2");
        VariableDeclarator bool3 = getVariable(compiled, "bool3");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(IntegerRange.ANY_VALUE, varState.getVariable(a));
        Assertions.assertEquals(new IntegerRange(6, 11), varState.getVariable(x));
        Assertions.assertEquals(new IntegerRange(-4, 10), varState.getVariable(y));
        Assertions.assertEquals(new IntegerRange(3), varState.getVariable(z));
        Assertions.assertEquals(BooleanValue.ANY_VALUE, varState.getVariable(bool));
        Assertions.assertEquals(BooleanValue.TRUE, varState.getVariable(bool2));
        Assertions.assertEquals(BooleanValue.FALSE, varState.getVariable(bool3));
    }

    @Test
    public void boxedBooleanOperatorsTest() {
        String code = """
                public class Main {
                    void test(Boolean a, Boolean b, Boolean c, boolean i) {
                        boolean j = true;
                        boolean k = false;
                        boolean l = a;
                        boolean m = !a;
                        boolean n = a && b;
                        boolean o = b || j;
                        boolean p = k || !c;
                        Boolean q = b;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        BlockStmt block = getBlockStatements(compiled).get(0);
        Parameter a = getParameter(compiled, "a");
        Parameter b = getParameter(compiled, "b");
        Parameter c = getParameter(compiled, "c");
        Parameter i = getParameter(compiled, "i");
        VariableDeclarator l = getVariable(compiled, "l");
        VariableDeclarator m = getVariable(compiled, "m");
        VariableDeclarator n = getVariable(compiled, "n");
        VariableDeclarator o = getVariable(compiled, "o");
        VariableDeclarator p = getVariable(compiled, "p");
        VariableDeclarator q = getVariable(compiled, "q");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(a, new BoxedPrimitive(BooleanValue.TRUE, true));
        varState.setVariable(b, new BoxedPrimitive(BooleanValue.FALSE, true));
        varState.setVariable(c, new BoxedPrimitive(BooleanValue.ANY_VALUE, true));
        block.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(new BoxedPrimitive(BooleanValue.TRUE), varState.getVariable(a));
        Assertions.assertEquals(new BoxedPrimitive(BooleanValue.FALSE), varState.getVariable(b));
        Assertions.assertEquals(new BoxedPrimitive(BooleanValue.ANY_VALUE), varState.getVariable(c));
        Assertions.assertEquals(BooleanValue.TRUE, varState.getVariable(l)); // requires implicit type cast
        Assertions.assertEquals(BooleanValue.FALSE, varState.getVariable(m));
        Assertions.assertEquals(BooleanValue.FALSE, varState.getVariable(n));
        Assertions.assertEquals(BooleanValue.TRUE, varState.getVariable(o));
        Assertions.assertEquals(BooleanValue.ANY_VALUE, varState.getVariable(p));
        Assertions.assertEquals(new BoxedPrimitive(BooleanValue.FALSE), varState.getVariable(q));
        Assertions.assertEquals(
                "NullPointerException: a",
                getVariableDeclarationErrors(analysisState.getErrorMap(), "l").stream().findFirst().get().getMessage()
        );
        Assertions.assertEquals(
                "NullPointerException: b",
                getVariableDeclarationErrors(analysisState.getErrorMap(), "n").stream().findFirst().get().getMessage()
        );
        Assertions.assertEquals(
                "NullPointerException: c",
                getVariableDeclarationErrors(analysisState.getErrorMap(), "p").stream().findFirst().get().getMessage()
        );

    }
}
