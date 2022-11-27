package analysis.visitor;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import analysis.values.EmptyValue;
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

public class BinaryExprTest {

    @BeforeEach
    public void runBefore() {
        AnalysisLogger.setLog(true);
    }

    @Test
    public void AddTest() {
        String code = """
                public class Main {
                    int test() {
                        int x = 100 + 200;
                        return x;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        VariableDeclarator x = getVariable(compiled, "x");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        IntegerRange sum = (IntegerRange) varState.getVariable(x);
        Assertions.assertEquals(100 + 200, sum.getMin());
        Assertions.assertEquals(100 + 200, sum.getMax());
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void divideZeroInRangeTest() {
        String code = """
                public class Main {
                    int test(int x, int y) {
                        int z = x / y;
                        return x;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        BlockStmt block = getBlockStatements(compiled).get(0);
        Parameter x = getParameter(compiled, "x");
        Parameter y = getParameter(compiled, "y");
        VariableDeclarator z = getVariable(compiled, "z");
        VariablesState varState = new VariablesState();
        varState.setVariable(x, new IntegerRange(20, 50));
        varState.setVariable(y, new IntegerRange(-10, 10));
        AnalysisState analysisState = new AnalysisState(varState);
        block.accept(new AnalysisVisitor(""), analysisState);
        IntegerRange quotient = (IntegerRange) varState.getVariable(z);
        Assertions.assertEquals(50 / -10, quotient.getMin());
        Assertions.assertEquals(50 / 10, quotient.getMax());
        Assertions.assertEquals(1, analysisState.getErrorMap().size());
    }

    @Test
    public void divideByZeroTest() {
        String code = """
                public class Main {
                    int test() {
                        int x = 1 / 0;
                        return x;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        VariableDeclarator x = getVariable(compiled, "x");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(new EmptyValue(), varState.getVariable(x));
        Assertions.assertEquals(1, analysisState.getErrorMap().size());
    }

    @Test
    public void divideByNonZeroTest() {
        String code = """
                public class Main {
                    int test() {
                        int x = 10 / 5;
                        return x;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        VariableDeclarator x = getVariable(compiled, "x");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        IntegerRange quotient = (IntegerRange) varState.getVariable(x);
        Assertions.assertEquals(10 / 5, quotient.getMin());
        Assertions.assertEquals(10 / 5, quotient.getMax());
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void AddMultiplyTest() {
        String code = """
                public class Main {
                    int test() {
                        int x = 10 * 20;
                        return x;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        VariableDeclarator x = getVariable(compiled, "x");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        IntegerRange product = (IntegerRange) varState.getVariable(x);
        Assertions.assertEquals(10 * 20, product.getMin());
        Assertions.assertEquals(10 * 20, product.getMax());
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void AddSubtractTest() {
        String code = """
                public class Main {
                    int test() {
                        int x = 10 - 5;
                        return x;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        VariableDeclarator x = getVariable(compiled, "x");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        IntegerRange diff = (IntegerRange) varState.getVariable(x);
        Assertions.assertEquals(10 - 5, diff.getMin());
        Assertions.assertEquals(10 - 5, diff.getMax());
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }
}
