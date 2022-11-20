package analysis.values.visitor;

import analysis.values.EmptyValue;
import analysis.values.IntegerRange;
import analysis.values.IntegerValue;
import analysis.values.PossibleValues;

public class RestrictEqualsVisitor extends RestrictionVisitor {
    @Override
    public PossibleValues visit(IntegerValue a, IntegerValue b) {
        if (a.getMin() > b.getMax()) return new EmptyValue();
        if (a.getMax() < b.getMin()) return new EmptyValue();
        return new IntegerRange(Integer.max(a.getMin(), b.getMin()), Integer.min(a.getMax(), b.getMax()));
    }

    // TODO: handle equals for different types
}
