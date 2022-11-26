package analysis.values;

import analysis.values.visitor.DivideVisitor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DivideTest {

    private DivideVisitor divideVisitor;

    @BeforeEach
    public void runBefore() {
        divideVisitor = new DivideVisitor();
    }

    public PossibleValues divide(PossibleValues a, PossibleValues b) {
        return a.acceptAbstractOp(divideVisitor, b);
    }

    /**
     * a.min > 0, a.max > 0, b.min > 0, b.max > 0
     *
     * min = a.min / b.max
     * max = a.max / b.min
     */
    @Test
    public void divideTest1() {
        PossibleValues x = new IntegerRange(1, 10);
        PossibleValues y = new IntegerRange(2, 20);
        IntegerRange xy = (IntegerRange)divide(x, y);
        Assertions.assertEquals(1 / 20, xy.getMin());
        Assertions.assertEquals(10 / 2, xy.getMax());
    }

    // TODO: remove
//    /**
//     * a.min > 0, a.max > 0, b.min < 0, b.max > 0
//     *
//     * min = a.max / b.min
//     * max = a.max / b.max
//     */
//    @Test
//    public void divideTest2() {
//        PossibleValues x = new IntegerRange(2, 40);
//        PossibleValues y = new IntegerRange(-4, 20);
//        IntegerRange xy = (IntegerRange)divide(x, y);
//        Assertions.assertEquals(40 / -4, xy.getMin());
//        Assertions.assertEquals(40 / 20, xy.getMax());
//    }

    /**
     * a.min > 0, a.max > 0, b.min < 0, b.max < 0
     *
     * min = a.max / b.max
     * max = a.min / b.min
     */
    @Test
    public void divideTest3() {
        PossibleValues x = new IntegerRange(2, 40);
        PossibleValues y = new IntegerRange(-10, -4);
        IntegerRange xy = (IntegerRange)divide(x, y);
        Assertions.assertEquals(40 / -4, xy.getMin());
        Assertions.assertEquals(2 / -10, xy.getMax());
    }

    /**
     * a.min < 0, a.max > 0, b.min > 0, b.max > 0
     *
     * min = a.min / b.min
     * max = a.max / a.min
     */
    @Test
    public void divideTest4() {
        PossibleValues x = new IntegerRange(-100, 50);
        PossibleValues y = new IntegerRange(10, 20);
        IntegerRange xy = (IntegerRange)divide(x, y);
        Assertions.assertEquals(-100 / 10, xy.getMin());
        Assertions.assertEquals(50 / 10, xy.getMax());
    }

    // TODO: remove
//    /**
//     * a.min < 0, a.max > 0, b.min < 0, b.max > 0
//     *
//     * min = min(a.min / b.max, a.max / b.min)
//     * max = max(a.min / b.min, a.max / b.max)
//     */
//    @Test
//    public void divideTest5() {
//        PossibleValues x = new IntegerRange(-100, 50);
//        PossibleValues y = new IntegerRange(-10, 20);
//        IntegerRange xy = (IntegerRange)divide(x, y);
//        Assertions.assertEquals(-100 / 20, xy.getMin());
//        Assertions.assertEquals(-100 / -10, xy.getMax());
//    }

    /**
     * a.min < 0, a.max > 0, b.min < 0, b.max < 0
     *
     * min = a.max / b.max
     * max = a.min / b.max
     */
    @Test
    public void divideTest6() {
        PossibleValues x = new IntegerRange(-100, 50);
        PossibleValues y = new IntegerRange(-20, -10);
        IntegerRange xy = (IntegerRange)divide(x, y);
        Assertions.assertEquals(50 / -10, xy.getMin());
        Assertions.assertEquals(-100 / -10, xy.getMax());
    }

    /**
     * a.min < 0, a.max < 0, b.min > 0, b.max > 0
     *
     * min = a.min / b.min
     * max = a.max / b.max
     */
    @Test
    public void divideTest7() {
        PossibleValues x = new IntegerRange(-100, -5);
        PossibleValues y = new IntegerRange(2, 20);
        IntegerRange xy = (IntegerRange)divide(x, y);
        Assertions.assertEquals(-100 / 2, xy.getMin());
        Assertions.assertEquals(-5 / 20, xy.getMax());
    }

    // TODO: remove
//    /**
//     * a.min < 0, a.max < 0, b.min < 0, b.max > 0
//     *
//     * min = a.min / b.max
//     * max = a.min / b.min
//     */
//    @Test
//    public void divideTest8() {
//        PossibleValues x = new IntegerRange(-100, -50);
//        PossibleValues y = new IntegerRange(-10, 20);
//        IntegerRange xy = (IntegerRange)divide(x, y);
//        Assertions.assertEquals(-100 / 20, xy.getMin());
//        Assertions.assertEquals(-100 / -10, xy.getMax());
//    }

    /**
     * a.min < 0, a.max < 0, b.min < 0, b.max < 0
     *
     * min = a.max / b.min
     * max = a.min / b.max
     */
    @Test
    public void divideTest9() {
        PossibleValues x = new IntegerRange(-10, -5);
        PossibleValues y = new IntegerRange(-20, -4);
        IntegerRange xy = (IntegerRange)divide(x, y);
        Assertions.assertEquals(-5 / -20, xy.getMin());
        Assertions.assertEquals(-10 / -4, xy.getMax());
    }
}
