package analysis.visitor;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import analysis.values.BoxedPrimitive;
import analysis.values.IntegerRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import logger.AnalysisLogger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static analysis.visitor.VisitorTestUtils.*;

public class UnaryOperatorTest {
    @BeforeEach
    public void runBefore() {
        AnalysisLogger.setLog(false);
    }

    @Test
    public void incrementDecrementTest() {
        String code = """
                public class Main {
                    void test() {
                        int x = 10;
                        int y = -4;
                        int a = x++;
                        int b = ++x;
                        int c = --y;
                        int d = y--;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        VariableDeclarator x = getVariable(compiled, "x");
        VariableDeclarator y = getVariable(compiled, "y");
        VariableDeclarator a = getVariable(compiled, "a");
        VariableDeclarator b = getVariable(compiled, "b");
        VariableDeclarator c = getVariable(compiled, "c");
        VariableDeclarator d = getVariable(compiled, "d");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(new IntegerRange(12), varState.getVariable(x));
        Assertions.assertEquals(new IntegerRange(-6), varState.getVariable(y));
        Assertions.assertEquals(new IntegerRange(10), varState.getVariable(a));
        Assertions.assertEquals(new IntegerRange(12), varState.getVariable(b));
        Assertions.assertEquals(new IntegerRange(-5), varState.getVariable(c));
        Assertions.assertEquals(new IntegerRange(-5), varState.getVariable(d));
    }

    @Test
    public void incrementDecrementIntegerWrapperTest() {
        String code = """
                public class Main {
                    void test(@Min(value = 10) @Max(value = 11) Integer x, @Min(value = -7) @Max(value = -4) Integer y) {
                        int a = x++;
                        Integer b = ++x;
                        int c = --y;
                        Integer d = y--;
                        int e = +x;
                        int f = -y;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        Parameter x = getParameter(compiled, "x");
        Parameter y = getParameter(compiled, "y");
        VariableDeclarator a = getVariable(compiled, "a");
        VariableDeclarator b = getVariable(compiled, "b");
        VariableDeclarator c = getVariable(compiled, "c");
        VariableDeclarator d = getVariable(compiled, "d");
        VariableDeclarator e = getVariable(compiled, "e");
        VariableDeclarator f = getVariable(compiled, "f");
        VariablesState varState = new VariablesState();
        varState.setVariable(x, new BoxedPrimitive(new IntegerRange(10), true));
        varState.setVariable(y, new BoxedPrimitive(new IntegerRange(-4), true));
        AnalysisState analysisState = new AnalysisState(varState);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(new BoxedPrimitive(new IntegerRange(12, 13)), varState.getVariable(x));
        Assertions.assertEquals(new BoxedPrimitive(new IntegerRange(-9, -6)), varState.getVariable(y));
        Assertions.assertEquals(new IntegerRange(10, 11), varState.getVariable(a));
        Assertions.assertEquals(new BoxedPrimitive(new IntegerRange(12, 13)), varState.getVariable(b));
        Assertions.assertEquals(new IntegerRange(-8, -5), varState.getVariable(c));
        Assertions.assertEquals(new BoxedPrimitive(new IntegerRange(-8, -5)), varState.getVariable(d));
        Assertions.assertEquals(new IntegerRange(12, 13), varState.getVariable(e));
        Assertions.assertEquals(new IntegerRange(6, 9), varState.getVariable(f));
    }
}
