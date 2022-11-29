package analysis.visitor;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import analysis.values.BoxedPrimitive;
import analysis.values.EmptyValue;
import analysis.values.IntegerRange;
import analysis.values.StringValue;
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
    private VariablesState variablesState;
    private AnalysisState analysisState;

    @BeforeEach
    public void runBefore() {
        variablesState = new NonEmptyVariablesState();
        analysisState = new AnalysisState(variablesState);
        AnalysisLogger.setLog(true);
    }

    @Test
    public void addTest() {
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
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        IntegerRange sum = (IntegerRange) variablesState.getVariable(x);
        Assertions.assertEquals(100 + 200, sum.getMin());
        Assertions.assertEquals(100 + 200, sum.getMax());
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void integerAddTest() {
        String code = """
                public class Main {
                    private Integer intField = 123;
                    void test(@Max(value=10) @Min(value=-20) Integer intParam) {
                        Integer a = null;
                        Integer b = 123;
                        Integer c = intField;
                        Integer x = a + a; 
                        Integer y = b - c; 
                        
                        Integer h = null;
                        Integer j = intField;
                        Integer k = intParam;
                        int z = 3 * h;
                        char v = j / 5;
                        int w = -40;
                        w *= k;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        VariableDeclarator a = getVariable(compiled, "a");
        VariableDeclarator b = getVariable(compiled, "b");
        VariableDeclarator c = getVariable(compiled, "c");
        VariableDeclarator h = getVariable(compiled, "h");
        VariableDeclarator j = getVariable(compiled, "j");
        VariableDeclarator x = getVariable(compiled, "x");
        VariableDeclarator y = getVariable(compiled, "y");
        VariableDeclarator z = getVariable(compiled, "z");
        VariableDeclarator v = getVariable(compiled, "v");
        VariableDeclarator w = getVariable(compiled, "w");
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(EmptyValue.VALUE, variablesState.getVariable(a));
        Assertions.assertEquals(BoxedPrimitive.create(new IntegerRange(123), false), variablesState.getVariable(b));
        Assertions.assertEquals(BoxedPrimitive.create(IntegerRange.ANY_VALUE, false), variablesState.getVariable(c));
        Assertions.assertEquals(EmptyValue.VALUE, variablesState.getVariable(h));
        Assertions.assertEquals(BoxedPrimitive.create(IntegerRange.ANY_VALUE, false), variablesState.getVariable(j));
        Assertions.assertEquals(EmptyValue.VALUE, variablesState.getVariable(x));
        Assertions.assertEquals(BoxedPrimitive.create(new IntegerRange(123 - Integer.MAX_VALUE, Integer.MAX_VALUE), false), variablesState.getVariable(y));
        Assertions.assertEquals(EmptyValue.VALUE, variablesState.getVariable(z));
        Assertions.assertEquals(new IntegerRange(Integer.MIN_VALUE / 5, Integer.MAX_VALUE / 5), variablesState.getVariable(v));
        Assertions.assertEquals(new IntegerRange(-400, 800), variablesState.getVariable(w));
        Assertions.assertEquals(5, analysisState.getErrorMap().size());
        Assertions.assertEquals(1, getVariableDeclarationErrors(analysisState.getErrorMap(), "x").size());
        Assertions.assertEquals(1, getVariableDeclarationErrors(analysisState.getErrorMap(), "y").size());
        Assertions.assertEquals(1, getVariableDeclarationErrors(analysisState.getErrorMap(), "z").size());
        Assertions.assertEquals(1, getVariableDeclarationErrors(analysisState.getErrorMap(), "v").size());
        Assertions.assertEquals(1, getVariableAssignmentErrors(analysisState.getErrorMap(), "w").size());
    }

    @Test
    public void stringAddTest() {
        String code = """
                public class Main {
                    int test() {
                        String a = null;
                        String b = "hello";
                        String x = a + a; 
                        String y = a + b; 
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        VariableDeclarator x = getVariable(compiled, "x");
        VariableDeclarator y = getVariable(compiled, "y");
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        String stringNull = null;
        String stringHello = "hello";
        String xExpected = stringNull + stringNull;
        String yExpected = stringNull + stringHello;
        Assertions.assertEquals(new StringValue(xExpected.length(), xExpected.length(), false), variablesState.getVariable(x));
        Assertions.assertEquals(new StringValue(yExpected.length(), yExpected.length(), false), variablesState.getVariable(y));
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
        variablesState.setVariable(x, new IntegerRange(20, 50));
        variablesState.setVariable(y, new IntegerRange(-10, 10));
        block.accept(new AnalysisVisitor(""), analysisState);
        IntegerRange quotient = (IntegerRange) variablesState.getVariable(z);
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
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        Assertions.assertEquals(new EmptyValue(), variablesState.getVariable(x));
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
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        IntegerRange quotient = (IntegerRange) variablesState.getVariable(x);
        Assertions.assertEquals(10 / 5, quotient.getMin());
        Assertions.assertEquals(10 / 5, quotient.getMax());
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void multiplyTest() {
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
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        IntegerRange product = (IntegerRange) variablesState.getVariable(x);
        Assertions.assertEquals(10 * 20, product.getMin());
        Assertions.assertEquals(10 * 20, product.getMax());
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void subtractTest() {
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
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        IntegerRange diff = (IntegerRange) variablesState.getVariable(x);
        Assertions.assertEquals(10 - 5, diff.getMin());
        Assertions.assertEquals(10 - 5, diff.getMax());
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }
}
