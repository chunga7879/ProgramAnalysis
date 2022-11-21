package analysis.values.visitor;

import analysis.values.EmptyValue;
import analysis.values.IntegerRange;
import analysis.values.IntegerValue;
import analysis.values.PossibleValues;

public class RestrictLessThanVisitor extends RestrictionVisitor {
    @Override
    public PossibleValues visit(IntegerValue a, IntegerValue b) {
        if (a.getMin() >= b.getMax()) return new EmptyValue();
        if (b.getMax() == Integer.MIN_VALUE) return new EmptyValue();
        return new IntegerRange(a.getMin(), Math.min(a.getMax(), b.getMax() - 1));
    }
}
