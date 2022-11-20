package analysis.values.visitor;

import analysis.values.AnyValue;
import analysis.values.IntegerRange;
import analysis.values.IntegerValue;
import analysis.values.PossibleValues;

public abstract class RestrictionVisitor extends OperationVisitorWithDefault {

    @Override
    public abstract PossibleValues visit(IntegerValue a, IntegerValue b);

    @Override
    public PossibleValues visit(IntegerValue a, AnyValue b) {
        return a;
    }

    @Override
    public PossibleValues visit(AnyValue a, IntegerValue b) {
        return visit(new IntegerRange(Integer.MIN_VALUE, Integer.MAX_VALUE), b);
    }
}
