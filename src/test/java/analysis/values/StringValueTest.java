package analysis.values;

import analysis.values.visitor.AddVisitor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringValueTest {
    @ParameterizedTest
    @CsvSource({"foo,bar,6,6", "abcdefg,123,10,10", "abc,123456,9,9"})
    public void TestAdStringString(String sa, String sb, int wantMin, int wantMax) {
        StringValue a = new StringValue(sa);
        StringValue b = new StringValue(sb);

        PossibleValues s = a.acceptAbstractOp(new AddVisitor(), b);
        assertEquals(wantMin, s.minStringLength());
        assertEquals(wantMax, s.maxStringLength());
    }

    @ParameterizedTest
    @CsvSource({"1,1,1,3,2,4", "10,9999,1,1,3,5"})
    public void TestAddStringIntegerRange(int imin, int imax, int smin, int smax, int wantMin, int wantMax) {
        IntegerRange a = new IntegerRange(imin, imax);
        StringValue b = new StringValue(smin, smax);

        PossibleValues s = a.acceptAbstractOp(new AddVisitor(), b);
        assertEquals(wantMin, s.minStringLength());
        assertEquals(wantMax, s.maxStringLength());

        s = b.acceptAbstractOp(new AddVisitor(), a);
        assertEquals(wantMin, s.minStringLength());
        assertEquals(wantMax, s.maxStringLength());
    }

    @Test
    public void TestAddStringNullValue() {
        StringValue a = new StringValue(1, 3);
        PossibleValues s = a.acceptAbstractOp(new AddVisitor(), NullValue.VALUE);
        assertEquals(1 + 4, s.minStringLength());
        assertEquals(3 + 4, s.maxStringLength());

        s = NullValue.VALUE.acceptAbstractOp(new AddVisitor(), a);
        assertEquals(1 + 4, s.minStringLength());
        assertEquals(3 + 4, s.maxStringLength());
    }

    @ParameterizedTest
    @CsvSource({
            "true,false,3,3,7,7",
            "false,true,3,3,8,8",
            "true,true,3,3,7,8"
    })
    public void TestAddStringBoolean(boolean t, boolean f, int smin, int smax, int wantMin, int wantMax) {
        StringValue a = new StringValue(smin, smax);
        BooleanValue b = new BooleanValue(t, f);

        PossibleValues s = a.acceptAbstractOp(new AddVisitor(), b);
        assertEquals(wantMin, s.minStringLength());
        assertEquals(wantMax, s.maxStringLength());

        s = b.acceptAbstractOp(new AddVisitor(), a);
        assertEquals(wantMin, s.minStringLength());
        assertEquals(wantMax, s.maxStringLength());
    }

    @ParameterizedTest
    @CsvSource({
            "a,2,3,3,4",
            "b,2,3,3,4",
    })
    public void TestAddStringBoolean(char c, int smin, int smax, int wantMin, int wantMax) {
        StringValue a = new StringValue(smin, smax);
        CharValue b = new CharValue(c);

        PossibleValues s = a.acceptAbstractOp(new AddVisitor(), b);
        assertEquals(wantMin, s.minStringLength());
        assertEquals(wantMax, s.maxStringLength());
    }
}
