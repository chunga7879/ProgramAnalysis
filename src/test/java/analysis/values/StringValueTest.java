package analysis.values;

import analysis.values.visitor.AddVisitor;
import analysis.values.visitor.MergeVisitor;
import analysis.values.visitor.RestrictEqualsVisitor;
import analysis.values.visitor.RestrictNotEqualsVisitor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class StringValueTest {
    @ParameterizedTest
    @CsvSource({"foo,bar,6,6", "abcdefg,123,10,10", "abc,123456,9,9"})
    public void TestAdStringString(String sa, String sb, int wantMin, int wantMax) {
        StringValue a = new StringValue(sa);
        StringValue b = new StringValue(sb);

        assertFalse(a.canBeNull());
        assertFalse(b.canBeNull());

        PossibleValues s = a.acceptAbstractOp(new AddVisitor(), b);
        assertEquals(wantMin, s.minStringLength());
        assertEquals(wantMax, s.maxStringLength());
        assertFalse(s.canBeNull());
    }

    @ParameterizedTest
    @CsvSource({
            "1,1,1,3,true,2,4",
            "10,9999,1,1,false,3,5"
    })
    public void TestAddStringIntegerRange(int imin, int imax, int smin, int smax, boolean snull, int wantMin, int wantMax) {
        IntegerRange a = new IntegerRange(imin, imax);
        StringValue b = new StringValue(smin, smax, snull);

        PossibleValues s = a.acceptAbstractOp(new AddVisitor(), b);
        assertEquals(wantMin, s.minStringLength());
        assertEquals(wantMax, s.maxStringLength());
        assertFalse(s.canBeNull());

        s = b.acceptAbstractOp(new AddVisitor(), a);
        assertEquals(wantMin, s.minStringLength());
        assertEquals(wantMax, s.maxStringLength());
        assertFalse(s.canBeNull());
    }

    @Test
    public void TestAddStringNullValue() {
        StringValue a = new StringValue(1, 3, false);
        PossibleValues s = a.acceptAbstractOp(new AddVisitor(), NullValue.VALUE);
        assertEquals(1 + 4, s.minStringLength());
        assertEquals(3 + 4, s.maxStringLength());

        s = NullValue.VALUE.acceptAbstractOp(new AddVisitor(), a);
        assertEquals(1 + 4, s.minStringLength());
        assertEquals(3 + 4, s.maxStringLength());
    }

    @ParameterizedTest
    @CsvSource({
            "true,false,3,3,false,7,7",
            "false,true,3,3,true,8,8",
            "true,true,3,3,false,7,8"
    })
    public void TestAddStringBoolean(boolean t, boolean f, int smin, int smax, boolean snull, int wantMin, int wantMax) {
        StringValue a = new StringValue(smin, smax, snull);
        BooleanValue b = new BooleanValue(t, f);

        PossibleValues s = a.acceptAbstractOp(new AddVisitor(), b);
        assertEquals(wantMin, s.minStringLength());
        assertEquals(wantMax, s.maxStringLength());
        assertFalse(s.canBeNull());

        s = b.acceptAbstractOp(new AddVisitor(), a);
        assertEquals(wantMin, s.minStringLength());
        assertEquals(wantMax, s.maxStringLength());
        assertFalse(s.canBeNull());
    }

    @ParameterizedTest
    @CsvSource({
            "a,2,3,false,3,4",
            "b,2,3,true,3,4",
    })
    public void TestAddStringBoolean(char c, int smin, int smax, boolean snull, int wantMin, int wantMax) {
        StringValue a = new StringValue(smin, smax, snull);
        CharValue b = new CharValue(c);

        PossibleValues s = a.acceptAbstractOp(new AddVisitor(), b);
        assertEquals(wantMin, s.minStringLength());
        assertEquals(wantMax, s.maxStringLength());
        assertFalse(s.canBeNull());
    }

    @ParameterizedTest
    @CsvSource({
            "10,20,true,5,30,false,5,30,true",
            "1,20,false,5,18,false,1,20,false"
    })
    public void TestStringMerge(int amin, int amax, boolean anull, int bmin, int bmax, boolean bnull, int wantMin, int wantMax, boolean wantNull) {
        StringValue a = new StringValue(amin, amax, anull);
        StringValue b = new StringValue(bmin, bmax, bnull);

        PossibleValues ab = a.acceptAbstractOp(new MergeVisitor(), b);
        assertEquals(wantMin, ab.minStringLength());
        assertEquals(wantMax, ab.maxStringLength());
        assertEquals(wantNull, ab.canBeNull());

    }

    @Test
    public void TestStringEquals() {
        StringValue a = new StringValue(10, 100, true);
        StringValue b = new StringValue(4, 18, false);
        StringValue c = new StringValue(100, 200, true);
        NullValue d = NullValue.VALUE;

        PossibleValues ab = a.acceptAbstractOp(new RestrictEqualsVisitor(), b);
        PossibleValues ac = a.acceptAbstractOp(new RestrictEqualsVisitor(), c);
        PossibleValues ad = a.acceptAbstractOp(new RestrictEqualsVisitor(), d);
        assertEquals(new StringValue(10, 18, false), ab);
        assertEquals(new StringValue(100, 100, true), ac);
        assertEquals(NullValue.VALUE, ad);
    }

    @Test
    public void TestStringNotEquals() {
        StringValue a = new StringValue(10, 100, true);
        NullValue b = NullValue.VALUE;

        PossibleValues ab = a.acceptAbstractOp(new RestrictNotEqualsVisitor(), b);
        assertEquals(new StringValue(10, 100, false), ab);
    }

}
