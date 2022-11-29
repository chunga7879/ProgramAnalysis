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

import static analysis.visitor.VisitorTestUtils.compile;
import static analysis.visitor.VisitorTestUtils.getVariable;

public class FieldAccessTest {
    private VariablesState variablesState;
    private AnalysisState analysisState;

    @BeforeEach
    public void runBefore() {
        variablesState = new VariablesState();
        analysisState = new AnalysisState(variablesState);
        AnalysisLogger.setLog(true);
    }

    @Test
    public void accessFields() {
        String code = """
                public class Main {
                    @Min(value = 10) @Max(value = 20)
                    public static int foo = 0;       
                    @Negative
                    public int foo2 = 0;                           
                    void test() {
                        int a = Main.foo;
                        int b = foo2;
                        int c = this.foo2;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        VariableDeclarator a = getVariable(compiled, "a");
        VariableDeclarator b = getVariable(compiled, "b");
        VariableDeclarator c = getVariable(compiled, "c");
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(new IntegerRange(10, 20), variablesState.getVariable(a));
        Assertions.assertEquals(new IntegerRange(Integer.MIN_VALUE, -1), variablesState.getVariable(b));
        Assertions.assertEquals(new IntegerRange(Integer.MIN_VALUE, -1), variablesState.getVariable(c));
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }
}
