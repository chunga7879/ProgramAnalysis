package analysis.values.visitor;

import analysis.values.EmptyValue;
import analysis.values.IntegerRange;
import analysis.values.IntegerValue;
import analysis.values.PossibleValues;

public class RestrictNotEqualsVisitor extends RestrictionVisitor {
    @Override
    public PossibleValues visit(IntegerValue a, IntegerValue b) {
        if (b.getMin() != b.getMax()) return a;
        if (b.getMin() == a.getMin() && b.getMin() == a.getMax()) return new EmptyValue();
        if (b.getMin() == a.getMin() && a.getMin() != Integer.MAX_VALUE) return new IntegerRange(a.getMin() + 1, a.getMax());
        if (b.getMin() == a.getMax() && a.getMax() != Integer.MIN_VALUE) return new IntegerRange(a.getMin(), a.getMax() - 1);
        return a;
    }
}
