package analysis.values;

import analysis.values.visitor.MergeVisitor;
import analysis.values.visitor.RestrictEqualsVisitor;
import analysis.values.visitor.RestrictNotEqualsVisitor;
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
        BooleanValue any = new BooleanValue(true, true);
        BooleanValue alwaysFalse = new BooleanValue(false, true);
        BooleanValue alwaysTrue = new BooleanValue(true, false);

        RestrictEqualsVisitor visitor = new RestrictEqualsVisitor();
        PossibleValues anyAndAny = any.acceptAbstractOp(visitor, any);
        PossibleValues anyAndFalse = any.acceptAbstractOp(visitor, alwaysFalse);
        PossibleValues anyAndTrue = any.acceptAbstractOp(visitor, alwaysTrue);
        PossibleValues falseAndAny = alwaysFalse.acceptAbstractOp(visitor, any);
        PossibleValues falseAndFalse = alwaysFalse.acceptAbstractOp(visitor, alwaysFalse);
        PossibleValues falseAndTrue = alwaysFalse.acceptAbstractOp(visitor, alwaysTrue);
        PossibleValues trueAndAny = alwaysTrue.acceptAbstractOp(visitor, any);
        PossibleValues trueAndFalse = alwaysTrue.acceptAbstractOp(visitor, alwaysFalse);
        PossibleValues trueAndTrue = alwaysTrue.acceptAbstractOp(visitor, alwaysTrue);

        Assertions.assertEquals(any, anyAndAny);
        Assertions.assertEquals(alwaysFalse, anyAndFalse);
        Assertions.assertEquals(alwaysTrue, anyAndTrue);
        Assertions.assertEquals(alwaysFalse, falseAndAny);
        Assertions.assertEquals(alwaysFalse, falseAndFalse);
        Assertions.assertEquals(EmptyValue.VALUE, falseAndTrue);
        Assertions.assertEquals(alwaysTrue, trueAndAny);
        Assertions.assertEquals(EmptyValue.VALUE, trueAndFalse);
        Assertions.assertEquals(alwaysTrue, trueAndTrue);
    }

    @Test
    public void restrictNotEqualsTest() {
        BooleanValue any = new BooleanValue(true, true);
        BooleanValue alwaysFalse = new BooleanValue(false, true);
        BooleanValue alwaysTrue = new BooleanValue(true, false);

        RestrictNotEqualsVisitor visitor = new RestrictNotEqualsVisitor();
        PossibleValues anyAndAny = any.acceptAbstractOp(visitor, any);
        PossibleValues anyAndFalse = any.acceptAbstractOp(visitor, alwaysFalse);
        PossibleValues anyAndTrue = any.acceptAbstractOp(visitor, alwaysTrue);
        PossibleValues falseAndAny = alwaysFalse.acceptAbstractOp(visitor, any);
        PossibleValues falseAndFalse = alwaysFalse.acceptAbstractOp(visitor, alwaysFalse);
        PossibleValues falseAndTrue = alwaysFalse.acceptAbstractOp(visitor, alwaysTrue);
        PossibleValues trueAndAny = alwaysTrue.acceptAbstractOp(visitor, any);
        PossibleValues trueAndFalse = alwaysTrue.acceptAbstractOp(visitor, alwaysFalse);
        PossibleValues trueAndTrue = alwaysTrue.acceptAbstractOp(visitor, alwaysTrue);

        Assertions.assertEquals(any, anyAndAny);
        Assertions.assertEquals(alwaysTrue, anyAndFalse);
        Assertions.assertEquals(alwaysFalse, anyAndTrue);
        Assertions.assertEquals(alwaysFalse, falseAndAny);
        Assertions.assertEquals(EmptyValue.VALUE, falseAndFalse);
        Assertions.assertEquals(alwaysFalse, falseAndTrue);
        Assertions.assertEquals(alwaysTrue, trueAndAny);
        Assertions.assertEquals(alwaysTrue, trueAndFalse);
        Assertions.assertEquals(EmptyValue.VALUE, trueAndTrue);
    }
}
