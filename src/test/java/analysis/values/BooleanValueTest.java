package analysis.values;

import analysis.values.visitor.MergeVisitor;
import analysis.values.visitor.RestrictEqualsVisitor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BooleanValueTest {
    @ParameterizedTest
    @CsvSource({
            // true / false
            "true,false,false,true,true,true",
            // false / false
            "false,true,false,true,false,true",
            // true / true
            "true,false,true,false,true,false",
    })
    public void TestMerge(boolean at, boolean af, boolean bt, boolean bf, boolean wt, boolean wf) {
        BooleanValue a = new BooleanValue(at, af);
        BooleanValue b = new BooleanValue(bt, bf);

        BooleanValue have = (BooleanValue) a.acceptOp(new MergeVisitor(), b);
        assertEquals(wt, have.canBeTrue());
        assertEquals(wf, have.canBeFalse());
    }

    @Test
    public void restrictEqualsTest() {
        BooleanValue a = new BooleanValue(true, true);
        BooleanValue b = new BooleanValue(false, true);
        BooleanValue c = new BooleanValue(true, false);

        PossibleValues aa = a.acceptAbstractOp(new RestrictEqualsVisitor(), a);
        PossibleValues ab = a.acceptAbstractOp(new RestrictEqualsVisitor(), b);
        PossibleValues ac = a.acceptAbstractOp(new RestrictEqualsVisitor(), c);
        PossibleValues ba = b.acceptAbstractOp(new RestrictEqualsVisitor(), a);
        PossibleValues bb = b.acceptAbstractOp(new RestrictEqualsVisitor(), b);
        PossibleValues bc = b.acceptAbstractOp(new RestrictEqualsVisitor(), c);
        PossibleValues ca = c.acceptAbstractOp(new RestrictEqualsVisitor(), a);
        PossibleValues cb = c.acceptAbstractOp(new RestrictEqualsVisitor(), b);
        PossibleValues cc = c.acceptAbstractOp(new RestrictEqualsVisitor(), c);

        Assertions.assertEquals(a, aa);
        Assertions.assertEquals(b, ab);
        Assertions.assertEquals(c, ac);
        Assertions.assertEquals(b, ba);
        Assertions.assertEquals(b, bb);
        Assertions.assertEquals(EmptyValue.VALUE, bc);
        Assertions.assertEquals(c, ca);
        Assertions.assertEquals(EmptyValue.VALUE, cb);
        Assertions.assertEquals(c, cc);

    }
}
