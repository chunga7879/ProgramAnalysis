package analysis.visitor;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import analysis.values.IntegerRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import logger.AnalysisLogger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static analysis.visitor.VisitorTestUtils.*;

public class ConditionalExprTest {
    @BeforeEach
    public void runBefore() {
        AnalysisLogger.setLog(false);
    }

    @Test
    public void conditionExprTest() {
        String code = """
                public class Main {
                    void test(int a) {
                        int v = 18 < 2 ? 4 : -22 + 3
                        int w = true ? 6 : -44;
                        int x = a > 0 ? (2 + 15) : 3;
                        int y = x;
                        int z = (a > 0 && (true || (x = 999) == 4)) ? (x = 100) : --x;                      
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        Parameter a = getParameter(compiled, "a");
        VariableDeclarator v = getVariable(compiled, "v");
        VariableDeclarator w = getVariable(compiled, "w");
        VariableDeclarator x = getVariable(compiled, "x");
        VariableDeclarator y = getVariable(compiled, "y");
        VariableDeclarator z = getVariable(compiled, "z");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(IntegerRange.ANY_VALUE, varState.getVariable(a));
        Assertions.assertEquals(new IntegerRange(-19), varState.getVariable(v));
        Assertions.assertEquals(new IntegerRange(6), varState.getVariable(w));
        Assertions.assertEquals(new IntegerRange(2, 100), varState.getVariable(x));
        Assertions.assertEquals(new IntegerRange(3, 17), varState.getVariable(y));
        Assertions.assertEquals(new IntegerRange(2, 100), varState.getVariable(z));
    }
}
