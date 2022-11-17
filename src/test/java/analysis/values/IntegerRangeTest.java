package analysis.values;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IntegerRangeTest {
    @Test
    public void mergeTest() {
        PossibleValues x = new IntegerRange(-10, 200);
        PossibleValues y = new IntegerRange(Integer.MIN_VALUE, 3);
        PossibleValues z = new IntegerRange(3000, 4000);
        IntegerRange xy = (IntegerRange)x.merge(y);
        IntegerRange xz = (IntegerRange)x.merge(z);
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
        IntegerRange xy = (IntegerRange)x.add(y);
        IntegerRange xz = (IntegerRange)x.add(z);
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
        IntegerRange xy = (IntegerRange)x.subtract(y);
        IntegerRange xz = (IntegerRange)x.subtract(z);
        Assertions.assertEquals(0, xy.getMin());
        Assertions.assertEquals(360, xy.getMax());
        Assertions.assertEquals(-109, xz.getMin());
        Assertions.assertEquals(180, xz.getMax());
    }
}
