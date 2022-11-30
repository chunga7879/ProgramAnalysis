package analysis.values;

import analysis.values.visitor.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IntegerRangeTest {
    private MergeVisitor mergeVisitor;
    private AddVisitor addVisitor;
    private SubtractVisitor subtractVisitor;
    private RestrictEqualsVisitor restrictEQVisitor;
    private RestrictGreaterThanVisitor restrictGTVisitor;
    private RestrictLessThanOrEqualVisitor restrictLTEVisitor;

    @BeforeEach
    public void runBefore() {
        mergeVisitor = new MergeVisitor();
        addVisitor = new AddVisitor();
        subtractVisitor = new SubtractVisitor();
        restrictEQVisitor = new RestrictEqualsVisitor();
        restrictGTVisitor = new RestrictGreaterThanVisitor();
        restrictLTEVisitor = new RestrictLessThanOrEqualVisitor();
    }

    public PossibleValues merge(PossibleValues a, PossibleValues b) {
        return a.acceptAbstractOp(mergeVisitor, b);
    }

    public PossibleValues add(PossibleValues a, PossibleValues b) {
        return a.acceptAbstractOp(addVisitor, b);
    }

    public PossibleValues subtract(PossibleValues a, PossibleValues b) {
        return a.acceptAbstractOp(subtractVisitor, b);
    }

    public PossibleValues restrictEQ(PossibleValues a, PossibleValues b) {
        return a.acceptAbstractOp(restrictEQVisitor, b);
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
        IntegerRange xy = (IntegerRange) merge(x, y);
        IntegerRange xz = (IntegerRange) merge(x, z);
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
        IntegerRange xy = (IntegerRange) add(x, y);
        IntegerRange xz = (IntegerRange) add(x, z);
        Assertions.assertEquals(-170, xy.getMin());
        Assertions.assertEquals(190, xy.getMax());
        Assertions.assertEquals(10, xz.getMin());
        Assertions.assertEquals(299, xz.getMax());
    }

    @Test
    public void subtractTest() {
        PossibleValues x = new IntegerRange(-10, 200);
        PossibleValues y = new IntegerRange(-160, -10);
        PossibleValues z = new IntegerRange(20, 99);
        IntegerRange xy = (IntegerRange) subtract(x, y);
        IntegerRange xz = (IntegerRange) subtract(x, z);
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
        IntegerRange xy = (IntegerRange) restrictGT(x, y);
        IntegerRange yx = (IntegerRange) restrictGT(y, x);
        IntegerRange zx = (IntegerRange) restrictGT(z, x);
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
        IntegerRange xy = (IntegerRange) restrictLTE(x, y);
        IntegerRange yx = (IntegerRange) restrictLTE(y, x);
        IntegerRange zx = (IntegerRange) restrictLTE(z, x);
        Assertions.assertEquals(-10, xy.getMin());
        Assertions.assertEquals(8, xy.getMax());
        Assertions.assertEquals(3, yx.getMin());
        Assertions.assertEquals(8, yx.getMax());
        Assertions.assertEquals(-10, zx.getMin());
        Assertions.assertEquals(-10, zx.getMax());
        Assertions.assertTrue(restrictLTE(x, e) instanceof EmptyValue);
    }

    @Test
    public void boxedIntegerAdd() {
        BoxedPrimitive a = new BoxedPrimitive(new IntegerRange(1));
        BoxedPrimitive b = new BoxedPrimitive(new IntegerRange(2));
        BoxedPrimitive ab = (BoxedPrimitive) add(a, b);
        Assertions.assertEquals(3, ((IntegerRange) ab.unbox()).getMin());
        Assertions.assertEquals(3, ((IntegerRange) ab.unbox()).getMax());
    }

    @Test
    public void boxedIntegerSubtract() {
        BoxedPrimitive a = new BoxedPrimitive(new IntegerRange(10));
        BoxedPrimitive b = new BoxedPrimitive(new IntegerRange(2));
        BoxedPrimitive ab = (BoxedPrimitive) subtract(a, b);
        Assertions.assertEquals(8, ((IntegerRange) ab.unbox()).getMin());
        Assertions.assertEquals(8, ((IntegerRange) ab.unbox()).getMax());
    }

    @Test
    public void boxedIntegerMerge() {
        BoxedPrimitive a = new BoxedPrimitive(new IntegerRange(1, 10));
        BoxedPrimitive b = new BoxedPrimitive(new IntegerRange(4, 20));
        BoxedPrimitive ab = (BoxedPrimitive) merge(a, b);
        Assertions.assertEquals(1, ((IntegerRange) ab.unbox()).getMin());
        Assertions.assertEquals(20, ((IntegerRange) ab.unbox()).getMax());
    }

    @Test
    public void boxedIntegerEquals() {
        BoxedPrimitive a = new BoxedPrimitive(new IntegerRange(1, 20), true);
        BoxedPrimitive b = new BoxedPrimitive(new IntegerRange(2, 27), true);
        BoxedPrimitive c = new BoxedPrimitive(new IntegerRange(-4, 16), false);
        BoxedPrimitive d = new BoxedPrimitive(new IntegerRange(-40, -20), true);
        IntegerValue e = new IntegerRange(14, 200);
        PossibleValues ab = restrictEQ(a, b);
        PossibleValues bc = restrictEQ(b, c);
        PossibleValues cd = restrictEQ(c, d);
        PossibleValues ce = restrictEQ(c, e);
        PossibleValues an = restrictEQ(a, NullValue.VALUE);
        PossibleValues cn = restrictEQ(c, NullValue.VALUE);
        Assertions.assertEquals(BoxedPrimitive.create(new IntegerRange(2, 20), true), ab);
        Assertions.assertEquals(BoxedPrimitive.create(new IntegerRange(2, 16), false), bc);
        Assertions.assertEquals(EmptyValue.VALUE, cd);
        Assertions.assertEquals(BoxedPrimitive.create(new IntegerRange(14, 16), false), ce);
        Assertions.assertEquals(NullValue.VALUE, an);
        Assertions.assertEquals(EmptyValue.VALUE, cn);
    }
}
