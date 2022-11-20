package analysis.values;

import analysis.values.visitor.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IntegerRangeTest {
    private MergeVisitor mergeVisitor;
    private AddVisitor addVisitor;
    private DivideVisitor divideVisitor;
    private MultiplyVisitor multiplyVisitor;
    private SubtractVisitor subtractVisitor;
    private RestrictGreaterThanVisitor restrictGTVisitor;
    private RestrictLessThanOrEqualVisitor restrictLTEVisitor;

    @BeforeEach
    public void runBefore() {
        mergeVisitor = new MergeVisitor();
        divideVisitor = new DivideVisitor();
        addVisitor = new AddVisitor();
        multiplyVisitor = new MultiplyVisitor();
        subtractVisitor = new SubtractVisitor();
        restrictGTVisitor = new RestrictGreaterThanVisitor();
        restrictLTEVisitor = new RestrictLessThanOrEqualVisitor();
    }

    public PossibleValues merge(PossibleValues a, PossibleValues b) {
        return a.acceptAbstractOp(mergeVisitor, b);
    }

    public PossibleValues add(PossibleValues a, PossibleValues b) {
        return a.acceptAbstractOp(addVisitor, b);
    }

    public PossibleValues divide(PossibleValues a, PossibleValues b) {
        return a.acceptAbstractOp(divideVisitor, b);
    }

    public PossibleValues multiply(PossibleValues a, PossibleValues b) {
        return a.acceptAbstractOp(multiplyVisitor, b);
    }

    public PossibleValues subtract(PossibleValues a, PossibleValues b) {
        return a.acceptAbstractOp(subtractVisitor, b);
    }

    public PossibleValues restrictGT(PossibleValues a, PossibleValues b) {
        return a.acceptAbstractOp(restrictGTVisitor, b);
    }

    public PossibleValues restrictLTE(PossibleValues a, PossibleValues b) {
        return a.acceptAbstractOp(restrictLTEVisitor, b);
    }

    @Test
    public void mergeTest() {
        PossibleValues x = new IntegerRange(-10, 200);
        PossibleValues y = new IntegerRange(Integer.MIN_VALUE, 3);
        PossibleValues z = new IntegerRange(3000, 4000);
        IntegerRange xy = (IntegerRange)merge(x, y);
        IntegerRange xz = (IntegerRange)merge(x, z);
        Assertions.assertEquals(Integer.MIN_VALUE, xy.getMin());
        Assertions.assertEquals(200, xy.getMax());
        Assertions.assertEquals(-10, xz.getMin());
        Assertions.assertEquals(4000, xz.getMax());
    }

    @Test
    public void addTest() {
        PossibleValues x = new IntegerRange(-10, 200);
        PossibleValues y = new IntegerRange(-160, -10);
        PossibleValues z = new IntegerRange(20, 99);
        IntegerRange xy = (IntegerRange)add(x, y);
        IntegerRange xz = (IntegerRange)add(x, z);
        Assertions.assertEquals(-170, xy.getMin());
        Assertions.assertEquals(190, xy.getMax());
        Assertions.assertEquals(10, xz.getMin());
        Assertions.assertEquals(299, xz.getMax());
    }

    @Test
    public void divideTest() {
        // TODO: implement
    }

    @Test
    public void multiplyTest() {
        PossibleValues x = new IntegerRange(-10, 100);
        PossibleValues y = new IntegerRange(-25, -10);
        PossibleValues z = new IntegerRange(30, 99);
        IntegerRange xy = (IntegerRange)multiply(x, y);
        IntegerRange xz = (IntegerRange)multiply(x, z);
        Assertions.assertEquals(-10 * -25, xy.getMin());
        Assertions.assertEquals(100 * -10, xy.getMax());
        Assertions.assertEquals(-10 * 30, xz.getMin());
        Assertions.assertEquals(100 * 99, xz.getMax());
    }

    @Test
    public void subtractTest() {
        PossibleValues x = new IntegerRange(-10, 200);
        PossibleValues y = new IntegerRange(-160, -10);
        PossibleValues z = new IntegerRange(20, 99);
        IntegerRange xy = (IntegerRange)subtract(x, y);
        IntegerRange xz = (IntegerRange)subtract(x, z);
        Assertions.assertEquals(0, xy.getMin());
        Assertions.assertEquals(360, xy.getMax());
        Assertions.assertEquals(-109, xz.getMin());
        Assertions.assertEquals(180, xz.getMax());
    }

    @Test
    public void restrictGreaterThanVisitorTest() {
        PossibleValues x = new IntegerRange(-10, 10);
        PossibleValues y = new IntegerRange(3, 8);
        PossibleValues z = new IntegerRange(-21, -9);
        PossibleValues e = new IntegerRange(-11, -10);
        IntegerRange xy = (IntegerRange)restrictGT(x, y);
        IntegerRange yx = (IntegerRange)restrictGT(y, x);
        IntegerRange zx = (IntegerRange)restrictGT(z, x);
        Assertions.assertEquals(4, xy.getMin());
        Assertions.assertEquals(10, xy.getMax());
        Assertions.assertEquals(3, yx.getMin());
        Assertions.assertEquals(8, yx.getMax());
        Assertions.assertEquals(-9, zx.getMin());
        Assertions.assertEquals(-9, zx.getMax());
        Assertions.assertTrue(restrictGT(e, x) instanceof EmptyValue);
    }

    @Test
    public void restrictLessThanOrEqualVisitorTest() {
        PossibleValues x = new IntegerRange(-10, 10);
        PossibleValues y = new IntegerRange(3, 8);
        PossibleValues z = new IntegerRange(-10, -10);
        PossibleValues e = new IntegerRange(-300, -11);
        IntegerRange xy = (IntegerRange)restrictLTE(x, y);
        IntegerRange yx = (IntegerRange)restrictLTE(y, x);
        IntegerRange zx = (IntegerRange)restrictLTE(z, x);
        Assertions.assertEquals(-10, xy.getMin());
        Assertions.assertEquals(8, xy.getMax());
        Assertions.assertEquals(3, yx.getMin());
        Assertions.assertEquals(8, yx.getMax());
        Assertions.assertEquals(-10, zx.getMin());
        Assertions.assertEquals(-10, zx.getMax());
        Assertions.assertTrue(restrictLTE(x, e) instanceof EmptyValue);
    }
}
