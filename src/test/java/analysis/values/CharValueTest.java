package analysis.values;

import analysis.values.visitor.*;
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

        CharValue i = (CharValue) a.acceptAbstractOp(new MergeVisitor(), b);

        assertEquals('a', (char) i.getMin());
        assertEquals('z', (char) i.getMax());
    }

    @Test
    public void TestEquals() {
        CharValue a = new CharValue('a', 'f');
        CharValue b = new CharValue('b', 'h');
        CharValue c = new CharValue('x', 'z');

        PossibleValues i = a.acceptAbstractOp(new RestrictEqualsVisitor(), b);
        PossibleValues j = a.acceptAbstractOp(new RestrictEqualsVisitor(), c);

        assertEquals(new CharValue('b', 'f'), i);
        assertEquals(EmptyValue.VALUE, j);
    }

    @Test
    public void TestNotEquals() {
        CharValue a = new CharValue('d', 't');
        CharValue b = new CharValue('t', 't');

        PossibleValues i = a.acceptAbstractOp(new RestrictNotEqualsVisitor(), b);
        PossibleValues j = b.acceptAbstractOp(new RestrictNotEqualsVisitor(), b);

        assertEquals(new CharValue('d', 's'), i);
        assertEquals(EmptyValue.VALUE, j);
    }

    @Test
    public void TestAddBoxedChars() {
        BoxedPrimitive a = new BoxedPrimitive(new CharValue('a'));
        BoxedPrimitive b = new BoxedPrimitive(new CharValue('b'));
        BoxedPrimitive ab = (BoxedPrimitive) a.acceptOp(new AddVisitor(), b);
        assertEquals('a' + 'b', ((IntegerRange) ab.unbox()).getMin());
        assertEquals('a' + 'b', ((IntegerRange) ab.unbox()).getMax());
    }
}
