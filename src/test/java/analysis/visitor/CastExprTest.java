package analysis.visitor;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
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

public class CastExprTest {
    private VariablesState variablesState;
    private AnalysisState analysisState;

    @BeforeEach
    public void runBefore() {
        variablesState = new NonEmptyVariablesState();
        analysisState = new AnalysisState(variablesState);
        AnalysisLogger.setLog(false);
    }

    @Test
    public void invalidCastIntegerToStringTest() {
        String code = """
                public class Main {
                    void test() {
                        Integer i = new Integer(10);
                        String s = (String) i;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(1, analysisState.getErrorMap().size());
    }

    @Test
    public void validCastIntToLongTest() {
        String code = """
                public class Main {
                    void test() {
                        int i = 5;
                        long l = (long) i;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void validCastLongToIntTest() {
        String code = """
                public class Main {
                    void test() {
                        long l = 5;
                        int i = (int) l;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void validCastIntToIntTest() {
        String code = """
                public class Main {
                    void test() {
                        int x = 5;
                        int y = (int) x + 1;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void validCastIntegerRangeTest() {
        String code = """
                public class Main {
                    void test(int x) {
                        int y = (int) x;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        BlockStmt block = getBlockStatements(compiled).get(0);
        Parameter x = getParameter(compiled, "x");
        variablesState.setVariable(x, new IntegerRange(1));
        block.accept(new AnalysisVisitor(""), analysisState);
        VariableDeclarator y = getVariable(compiled, "y");
        IntegerRange val = (IntegerRange) variablesState.getVariable(y);
        Assertions.assertEquals(1, val.getMin());
        Assertions.assertEquals(1, val.getMax());
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }
}
