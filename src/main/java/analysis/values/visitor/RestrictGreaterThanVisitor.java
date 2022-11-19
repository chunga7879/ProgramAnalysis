package analysis.values.visitor;

import analysis.values.EmptyValue;
import analysis.values.IntegerRange;
import analysis.values.IntegerValue;
import analysis.values.PossibleValues;

public class RestrictGreaterThanVisitor extends RestrictionVisitor {
    @Override
    public PossibleValues visit(IntegerValue a, IntegerValue b) {
        if (a.getMax() <= b.getMin()) return new EmptyValue();
        return new IntegerRange(Math.max(a.getMin(), b.getMin() + 1), a.getMax());
    }
}
