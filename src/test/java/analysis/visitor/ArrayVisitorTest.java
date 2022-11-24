package analysis.visitor;

import analysis.model.AnalysisState;
import analysis.model.VariablesState;
import analysis.values.ArrayValue;
import analysis.values.IntegerRange;
import analysis.values.IntegerValue;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.stmt.BlockStmt;
import logger.AnalysisLogger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static analysis.visitor.VisitorTestUtils.*;

public class ArrayVisitorTest {
    @BeforeEach
    public void runBefore() {
        AnalysisLogger.setLog(true);
    }

    @Test
    public void ArrayCreationTest() {
        String code = """
                public class Main {
                    void main(int length) {
                        int[] a = new int[5];
                        int[] b = new int[length];
                        int[] c = new int[] { 2, 3, 4 };
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        Parameter length = getParameter(compiled, "length");
        VariableDeclarator a = getVariable(compiled, "a");
        VariableDeclarator b = getVariable(compiled, "b");
        VariableDeclarator c = getVariable(compiled, "c");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(length, new IntegerRange(-10, 10));
        BlockStmt block = getBlockStatements(compiled).get(0);
        block.accept(new AnalysisVisitor(""), analysisState);
        ArrayValue aValue = (ArrayValue) varState.getVariable(a);
        ArrayValue bValue = (ArrayValue) varState.getVariable(b);
        ArrayValue cValue = (ArrayValue) varState.getVariable(c);
        IntegerValue lengthValue = (IntegerValue) varState.getVariable(length);
        Assertions.assertEquals(new IntegerRange(5), aValue.getLength());
        Assertions.assertFalse(aValue.canBeNull());
        Assertions.assertEquals(new IntegerRange(0, 10), bValue.getLength());
        Assertions.assertFalse(aValue.canBeNull());
        Assertions.assertEquals(ArrayValue.DEFAULT_LENGTH, cValue.getLength());
        Assertions.assertFalse(aValue.canBeNull());
        // TODO: restrict variable after exception
        // Assertions.assertEquals(new IntegerRange(0, 10), lengthValue);
        Assertions.assertEquals(new IntegerRange(-10, 10), lengthValue);
    }

    @Test
    public void ArrayIndexTest() {
        String code = """
                public class Main {
                    void main(int length) {
                        int[] a = new int[length];
                        int b = a[length + 1];
                        int c = a[length - 1];
                        int d = a.length;
                        if (a[length + 1] > 2) {
                        }
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        Parameter length = getParameter(compiled, "length");
        VariableDeclarator a = getVariable(compiled, "a");
        VariableDeclarator b = getVariable(compiled, "b");
        VariableDeclarator c = getVariable(compiled, "c");
        VariableDeclarator d = getVariable(compiled, "d");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(length, new IntegerRange(5, 10));
        BlockStmt block = getBlockStatements(compiled).get(0);
        block.accept(new AnalysisVisitor(""), analysisState);
        ArrayValue aValue = (ArrayValue) varState.getVariable(a);
        IntegerValue bValue = (IntegerValue) varState.getVariable(b);
        IntegerValue cValue = (IntegerValue) varState.getVariable(c);
        IntegerValue dValue = (IntegerValue) varState.getVariable(d);
        IntegerValue lengthValue = (IntegerValue) varState.getVariable(length);
        Assertions.assertEquals(new IntegerRange(5, 10), aValue.getLength());
        Assertions.assertFalse(aValue.canBeNull());
        Assertions.assertEquals(IntegerRange.ANY_VALUE, bValue);
        Assertions.assertEquals(IntegerRange.ANY_VALUE, cValue);
        Assertions.assertEquals(new IntegerRange(5, 10), dValue);
    }

    @Test
    public void ArrayMergeTest() {
        String code = """
                public class Main {
                    void main(int x) {
                        String[] a;
                        if (x > 0) a = new String[5];
                        else a = new String[40];
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        Parameter x = getParameter(compiled, "x");
        VariableDeclarator a = getVariable(compiled, "a");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(x, new IntegerRange(-20, 30));
        BlockStmt block = getBlockStatements(compiled).get(0);
        block.accept(new AnalysisVisitor(""), analysisState);
        ArrayValue aValue = (ArrayValue) varState.getVariable(a);
        Assertions.assertEquals(new IntegerRange(5, 40), aValue.getLength());
        Assertions.assertFalse(aValue.canBeNull());
    }

    @Test
    public void ArrayLengthTest() {
        String code = """
                public class Main {
                    void main(int x) {
                        boolean[] a = new boolean[x];
                        int b = 10;
                        if (a.length > 10 && 20 > a.length) {
                          b = a.length;
                        }
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        Parameter x = getParameter(compiled, "x");
        VariableDeclarator a = getVariable(compiled, "a");
        VariableDeclarator b = getVariable(compiled, "b");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(x, new IntegerRange(0, 30));
        BlockStmt block = getBlockStatements(compiled).get(0);
        block.accept(new AnalysisVisitor(""), analysisState);
        ArrayValue aValue = (ArrayValue) varState.getVariable(a);
        IntegerValue bValue = (IntegerValue) varState.getVariable(b);
        Assertions.assertEquals(new IntegerRange(0, 30), aValue.getLength());
        Assertions.assertFalse(aValue.canBeNull());
        Assertions.assertEquals(new IntegerRange(10, 19), bValue);
    }
}
