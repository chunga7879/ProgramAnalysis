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

public class ArrayVisitorTest {
    @BeforeEach
    public void runBefore() {
        AnalysisLogger.setLog(false);
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
        Assertions.assertEquals(new IntegerRange(0, 10), lengthValue);
    }

    @Test
    public void ArrayIndexTest() {
        String code = """
                public class Main {
                    void main(int length, int index) {
                        int[] a = new int[length];
                        int b = a[index];
                        int c = a[index - 1];
                        int d = a.length;
                        if (a[index] > 2) {
                        }
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        Parameter length = getParameter(compiled, "length");
        Parameter index = getParameter(compiled, "index");
        VariableDeclarator a = getVariable(compiled, "a");
        VariableDeclarator b = getVariable(compiled, "b");
        VariableDeclarator c = getVariable(compiled, "c");
        VariableDeclarator d = getVariable(compiled, "d");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(length, new IntegerRange(5, 10));
        varState.setVariable(index, new IntegerRange(6, 10));
        BlockStmt block = getBlockStatements(compiled).get(0);
        block.accept(new AnalysisVisitor(""), analysisState);
        ArrayValue aValue = (ArrayValue) varState.getVariable(a);
        IntegerValue bValue = (IntegerValue) varState.getVariable(b);
        IntegerValue cValue = (IntegerValue) varState.getVariable(c);
        IntegerValue dValue = (IntegerValue) varState.getVariable(d);
        Assertions.assertEquals(new IntegerRange(5, 10), varState.getVariable(length));
        Assertions.assertEquals(new IntegerRange(6, 9), varState.getVariable(index));
        Assertions.assertEquals(new IntegerRange(7, 10), aValue.getLength());
        Assertions.assertFalse(aValue.canBeNull());
        Assertions.assertEquals(IntegerRange.ANY_VALUE, bValue);
        Assertions.assertEquals(IntegerRange.ANY_VALUE, cValue);
        Assertions.assertEquals(new IntegerRange(7, 10), dValue);
        Assertions.assertEquals(3, analysisState.getErrorMap().size());
    }

    @Test
    public void ArrayMergeTest() {
        String code = """
                public class Main {
                    void main(int x) {
                        String[] a;
                        long[] b = null;
                        if (x > 0) a = new String[5];
                        else {
                            a = new String[40];
                            b = new long[3];
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
        varState.setVariable(x, new IntegerRange(-20, 30));
        BlockStmt block = getBlockStatements(compiled).get(0);
        block.accept(new AnalysisVisitor(""), analysisState);
        ArrayValue aValue = (ArrayValue) varState.getVariable(a);
        Assertions.assertEquals(new IntegerRange(5, 40), aValue.getLength());
        Assertions.assertFalse(aValue.canBeNull());
        ArrayValue bValue = (ArrayValue) varState.getVariable(b);
        Assertions.assertEquals(new IntegerRange(3, 3), bValue.getLength());
        Assertions.assertTrue(bValue.canBeNull());
    }

    @Test
    public void ArrayLengthTest() {
        String code = """
                public class Main {
                    void main(int x, int y) {
                        boolean[] arr1 = new boolean[x];
                        byte[] arr2 = new byte[y];
                        int i = 10;
                        if (arr1.length > 10 && 20 > arr1.length) {
                          i = arr1.length;
                        }
                    }
                }
                """;
        CompilationUnit compiled = compile(code);
        Parameter x = getParameter(compiled, "x");
        Parameter y = getParameter(compiled, "y");
        VariableDeclarator arr1 = getVariable(compiled, "arr1");
        VariableDeclarator arr2 = getVariable(compiled, "arr2");
        VariableDeclarator i = getVariable(compiled, "i");
        VariablesState varState = new VariablesState();
        AnalysisState analysisState = new AnalysisState(varState);
        varState.setVariable(x, new IntegerRange(0, 30));
        varState.setVariable(y, new AnyValue());
        BlockStmt block = getBlockStatements(compiled).get(0);
        block.accept(new AnalysisVisitor(""), analysisState);
        ArrayValue arr1Value = (ArrayValue) varState.getVariable(arr1);
        Assertions.assertEquals(new IntegerRange(0, 30), arr1Value.getLength());
        Assertions.assertFalse(arr1Value.canBeNull());
        ArrayValue arr2Value = (ArrayValue) varState.getVariable(arr2);
        Assertions.assertEquals(ArrayValue.DEFAULT_LENGTH, arr2Value.getLength());
        Assertions.assertFalse(arr2Value.canBeNull());
        Assertions.assertEquals(new IntegerRange(10, 19), varState.getVariable(i));
    }
}
