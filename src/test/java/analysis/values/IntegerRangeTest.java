package analysis.values;

import analysis.values.visitor.AddVisitor;
import analysis.values.visitor.MergeVisitor;
import analysis.values.visitor.SubtractVisitor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IntegerRangeTest {
    private MergeVisitor mergeVisitor;
    private AddVisitor addVisitor;
    private SubtractVisitor subtractVisitor;

    @BeforeEach
    public void runBefore() {
        mergeVisitor = new MergeVisitor();
        addVisitor = new AddVisitor();
        subtractVisitor = new SubtractVisitor();
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
}
