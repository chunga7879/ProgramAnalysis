package analysis.visitor;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import analysis.values.*;
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
        PossibleValues s = variablesState.getVariable(getVariable(compiled, "s"));
        Assertions.assertTrue(s.isEmpty());
        Assertions.assertEquals(1, analysisState.getErrorMap().size());
    }

    @Test
    public void invalidCastObjectToIntegerTest() {
        String code = """
                public class Main {
                    void test() {
                        Object a = "1";
                        Integer b = (Integer) a;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        PossibleValues a = variablesState.getVariable(getVariable(compiled, "b"));
        Assertions.assertTrue(a.isEmpty());
        Assertions.assertEquals(1, analysisState.getErrorMap().size());
    }

    @Test
    public void invalidPrimitiveCastTest() {
        String code = """
                public class Main {
                    void test() {
                        int a = (int) true;
                        boolean b = (boolean) 5;
                        boolean c = (boolean) 'a';
                        char d = (char) true;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        PossibleValues a = variablesState.getVariable(getVariable(compiled, "a"));
        PossibleValues b = variablesState.getVariable(getVariable(compiled, "b"));
        PossibleValues c = variablesState.getVariable(getVariable(compiled, "c"));
        PossibleValues d = variablesState.getVariable(getVariable(compiled, "d"));
        Assertions.assertTrue(a.isEmpty());
        Assertions.assertTrue(b.isEmpty());
        Assertions.assertTrue(c.isEmpty());
        Assertions.assertTrue(d.isEmpty());
        Assertions.assertEquals(4, analysisState.getErrorMap().size());
    }

    @Test
    public void validCastIntegerIntTest() {
        String code = """
                public class Main {
                    void test() {
                        int a = 5;
                        Integer b = (Integer) a;
                        Integer c = new Integer(10);
                        int d = (int) c;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        BoxedPrimitive box = (BoxedPrimitive) variablesState.getVariable(getVariable(compiled, "b"));
        IntegerRange b = (IntegerRange) box.unbox();
        IntegerRange d = (IntegerRange) variablesState.getVariable(getVariable(compiled, "d"));
        Assertions.assertEquals(5, b.getMax());
        Assertions.assertEquals(5, b.getMax());
        Assertions.assertEquals(10, d.getMin());
        Assertions.assertEquals(10, d.getMax());
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void validCastCharacterCharTest() {
        String code = """
                public class Main {
                    void test() {
                        char a = 'a';
                        Character b = (char) a;
                        Character c = new Character('c');
                        char d = (char) c;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        BoxedPrimitive box = (BoxedPrimitive) variablesState.getVariable(getVariable(compiled, "b"));
        CharValue b = (CharValue) box.unbox();
        CharValue d = (CharValue) variablesState.getVariable(getVariable(compiled, "d"));
        Assertions.assertEquals('a', b.getMax());
        Assertions.assertEquals('a', b.getMax());
        Assertions.assertEquals('c', d.getMin());
        Assertions.assertEquals('c', d.getMax());
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void validCastCharIntTest() {
        String code = """
                public class Main {
                    void test() {
                        int a = 5;
                        char b = (char) 5;
                        char c = 'a';
                        int d = (int) c;
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        CharValue b = (CharValue) variablesState.getVariable(getVariable(compiled, "b"));
        IntegerRange d = (IntegerRange) variablesState.getVariable(getVariable(compiled, "d"));
        Assertions.assertEquals(5, b.getMin());
        Assertions.assertEquals(5, b.getMax());
        Assertions.assertEquals(97, d.getMin());
        Assertions.assertEquals(97, d.getMax());
        Assertions.assertEquals(0, analysisState.getErrorMap().size());
    }

    @Test
    public void validCastWithErrorExpression() {
        String code = """
                public class Main {
                    void test() {
                        int a = 5;
                        char b = (char) (a / 0);
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        compiled.accept(new AnalysisVisitor("test"), analysisState);
        PossibleValues b = variablesState.getVariable(getVariable(compiled, "b"));
        Assertions.assertTrue(b.isEmpty());
        Assertions.assertEquals(1, analysisState.getErrorMap().size());
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
        IntegerRange y = (IntegerRange) variablesState.getVariable(getVariable(compiled, "y"));
        Assertions.assertEquals(5 + 1, y.getMin());
        Assertions.assertEquals(5 + 1, y.getMax());
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
