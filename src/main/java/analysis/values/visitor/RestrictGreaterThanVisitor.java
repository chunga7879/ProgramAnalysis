package analysis.values.visitor;

import analysis.values.*;

public class RestrictGreaterThanVisitor extends RestrictionVisitor {
    @Override
    public PossibleValues visit(IntegerValue a, IntegerValue b) {
        if (a.getMax() <= b.getMin()) return new EmptyValue();
        if (b.getMin() == Integer.MAX_VALUE) return new EmptyValue();
        return new IntegerRange(Math.max(a.getMin(), b.getMin() + 1), a.getMax());
    }
}
