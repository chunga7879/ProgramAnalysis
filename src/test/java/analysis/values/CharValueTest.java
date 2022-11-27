package analysis.values;

import analysis.values.visitor.AddVisitor;
import analysis.values.visitor.MergeVisitor;
import analysis.values.visitor.SubtractVisitor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CharValueTest {

    @Test
    public void TestAddCharChar() {
        CharValue a = new CharValue('a');
        CharValue b = new CharValue('b');

        IntegerValue i = (IntegerValue) a.acceptAbstractOp(new AddVisitor(), b);

        assertEquals('a' + 'b', i.getMin());
        assertEquals('a' + 'b', i.getMax());
    }

    @Test
    public void TestAddCharIntRange() {
        CharValue a = new CharValue('a');
        IntegerRange b = new IntegerRange(10, 12);

        IntegerValue i = (IntegerValue) a.acceptAbstractOp(new AddVisitor(), b);
        assertEquals(10 + 'a', i.getMin());
        assertEquals(12 + 'a', i.getMax());

        i = (IntegerValue) b.acceptAbstractOp(new AddVisitor(), a);
        assertEquals(10 + 'a', i.getMin());
        assertEquals(12 + 'a', i.getMax());
    }

    @Test
    public void TestSubtractCharChar() {
        CharValue a = new CharValue('z');
        CharValue b = new CharValue('a');

        IntegerValue i = (IntegerValue) a.acceptAbstractOp(new SubtractVisitor(), b);

        assertEquals('z' - 'a', i.getMin());
        assertEquals('z' - 'a', i.getMax());
    }

    @Test
    public void TestSubtractCharIntRange() {
        CharValue a = new CharValue('a');
        IntegerRange b = new IntegerRange(10, 12);

        IntegerValue i = (IntegerValue) a.acceptAbstractOp(new SubtractVisitor(), b);
        assertEquals('a' - 12, i.getMin());
        assertEquals('a' - 10, i.getMax());

        i = (IntegerValue) b.acceptAbstractOp(new SubtractVisitor(), a);
        assertEquals(10 - 'a', i.getMin());
        assertEquals(12 - 'a', i.getMax());
    }

    @Test
    public void TestMerge() {
        CharValue a = new CharValue('a');
        CharValue b = new CharValue('z');

        IntegerValue i = (IntegerValue) a.acceptAbstractOp(new MergeVisitor(), b);

        assertEquals('a', i.getMin());
        assertEquals('z', i.getMax());
    }

    @Test
    public void TestAddBoxedChars() {
        BoxedPrimitive<CharValue> a = new BoxedPrimitive<>(new CharValue('a'));
        BoxedPrimitive<CharValue> b = new BoxedPrimitive<>(new CharValue('b'));
        BoxedPrimitive<IntegerRange> ab = (BoxedPrimitive<IntegerRange>) a.acceptOp(new AddVisitor(), b);
        assertEquals('a' + 'b', ab.unbox().getMin());
        assertEquals('a' + 'b', ab.unbox().getMax());
    }
}
