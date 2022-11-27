package analysis.values;

import analysis.values.visitor.MultiplyVisitor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MultiplyTest {

    private MultiplyVisitor multiplyVisitor;

    @BeforeEach
    public void runBefore() {
        multiplyVisitor = new MultiplyVisitor();
    }

    public PossibleValues multiply(PossibleValues a, PossibleValues b) {
        return a.acceptAbstractOp(multiplyVisitor, b);
    }

    /**
     * a.min > 0, a.max > 0, b.min > 0, b.max > 0
     *
     * min = a.min * b.min
     * max = a.max * b.max
     */
    @Test
    public void multiplyTest1() {
        PossibleValues x = new IntegerRange(1, 10);
        PossibleValues y = new IntegerRange(2, 20);
        IntegerRange xy = (IntegerRange)multiply(x, y);
        Assertions.assertEquals(1 * 2, xy.getMin());
        Assertions.assertEquals(10 * 20, xy.getMax());
    }

    /**
     * a.min > 0, a.max > 0, b.min < 0, b.max > 0
     *
     * min = a.max * b.min
     * max = a.max * b.max
     */
    @Test
    public void multiplyTest2() {
        PossibleValues x = new IntegerRange(1, 10);
        PossibleValues y = new IntegerRange(-1, 20);
        IntegerRange xy = (IntegerRange)multiply(x, y);
        Assertions.assertEquals(10 * -1, xy.getMin());
        Assertions.assertEquals(10 * 20, xy.getMax());
    }

    /**
     * a.min > 0, a.max > 0, b.min < 0, b.max < 0
     *
     * min = a.max * b.min
     * max = a.min * b.max
     */
    @Test
    public void multiplyTest3() {
        PossibleValues x = new IntegerRange(1, 10);
        PossibleValues y = new IntegerRange(-2, -1);
        IntegerRange xy = (IntegerRange)multiply(x, y);
        Assertions.assertEquals(10 * -2, xy.getMin());
        Assertions.assertEquals(1 * -1, xy.getMax());
    }

    /**
     * a.min < 0, a.max > 0, b.min > 0, b.max > 0
     *
     * min = a.min * b.max
     * max = a.max * b.max
     */
    @Test
    public void multiplyTest4() {
        PossibleValues x = new IntegerRange(-1, 10);
        PossibleValues y = new IntegerRange(2, 15);
        IntegerRange xy = (IntegerRange)multiply(x, y);
        Assertions.assertEquals(-1 * 15, xy.getMin());
        Assertions.assertEquals(10 * 15, xy.getMax());
    }

    /**
     * a.min < 0, a.max > 0, b.min < 0, b.max > 0
     *
     * min = min(a.min * b.max, a.max * b.min)
     * max = max(a.min * b.min, a.max * b.max)
     */
    @Test
    public void multiplyTest5() {
        PossibleValues x = new IntegerRange(-1, 10);
        PossibleValues y = new IntegerRange(-2, 20);
        IntegerRange xy = (IntegerRange)multiply(x, y);
        Assertions.assertEquals(-1 * 20, xy.getMin());
        Assertions.assertEquals(10 * 20, xy.getMax());
    }

    /**
     * a.min < 0, a.max > 0, b.min < 0, b.max < 0
     *
     * min = a.max * b.min
     * max = a.min * b.min
     */
    @Test
    public void multiplyTest6() {
        PossibleValues x = new IntegerRange(-1, 10);
        PossibleValues y = new IntegerRange(-3, -2);
        IntegerRange xy = (IntegerRange)multiply(x, y);
        Assertions.assertEquals(10 * -3, xy.getMin());
        Assertions.assertEquals(-1 * -3, xy.getMax());
    }

    /**
     * a.min < 0, a.max < 0, b.min > 0, b.max > 0
     *
     * min = a.min * b.max
     * max = a.max * b.min
     */
    @Test
    public void multiplyTest7() {
        PossibleValues x = new IntegerRange(-2, -1);
        PossibleValues y = new IntegerRange(3, 10);
        IntegerRange xy = (IntegerRange)multiply(x, y);
        Assertions.assertEquals(-2 * 10, xy.getMin());
        Assertions.assertEquals(-1 * 3, xy.getMax());
    }

    /**
     * a.min < 0, a.max < 0, b.min < 0, b.max > 0
     *
     * min = a.min * b.max
     * max = a.min * b.min
     */
    @Test
    public void multiplyTest8() {
        PossibleValues x = new IntegerRange(-10, -1);
        PossibleValues y = new IntegerRange(-5, 20);
        IntegerRange xy = (IntegerRange)multiply(x, y);
        Assertions.assertEquals(-10 * 20, xy.getMin());
        Assertions.assertEquals(-10 * -5, xy.getMax());
    }

    /**
     * a.min < 0, a.max < 0, b.min < 0, b.max < 0
     *
     * min = a.max * b.max
     * max = a.min * b.min
     */
    @Test
    public void multiplyTest9() {
        PossibleValues x = new IntegerRange(-10, -1);
        PossibleValues y = new IntegerRange(-20, -5);
        IntegerRange xy = (IntegerRange)multiply(x, y);
        Assertions.assertEquals(-1 * -5, xy.getMin());
        Assertions.assertEquals(-10 * -20, xy.getMax());
    }

    /**
     * a.min == 0, a.max > 0, b.min > 0, b.max > 0
     *
     * min = a.min * b.min
     * max = a.max * b.max
     */
    @Test
    public void multiplyTest10() {
        PossibleValues x = new IntegerRange(0, 1);
        PossibleValues y = new IntegerRange(2, 3);
        IntegerRange xy = (IntegerRange)multiply(x, y);
        Assertions.assertEquals(0 * 2, xy.getMin());
        Assertions.assertEquals(1 * 3, xy.getMax());
    }
}
