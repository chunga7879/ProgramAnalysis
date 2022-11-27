package analysis.values;

import analysis.values.visitor.MergeVisitor;
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
}
