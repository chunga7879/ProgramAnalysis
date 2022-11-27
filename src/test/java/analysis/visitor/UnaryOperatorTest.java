package analysis.visitor;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import analysis.values.IntegerRange;
import com.github.javaparser.ast.CompilationUnit;
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
}
