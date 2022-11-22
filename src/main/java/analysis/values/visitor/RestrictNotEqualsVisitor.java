package analysis.values.visitor;

import analysis.values.*;

public class RestrictNotEqualsVisitor extends RestrictionVisitor {
    @Override
    public PossibleValues visit(IntegerValue a, IntegerValue b) {
        if (b.getMin() != b.getMax()) return a;
        if (b.getMin() == a.getMin() && b.getMin() == a.getMax()) return new EmptyValue();
        if (b.getMin() == a.getMin() && a.getMin() != Integer.MAX_VALUE) return new IntegerRange(a.getMin() + 1, a.getMax());
        if (b.getMin() == a.getMax() && a.getMax() != Integer.MIN_VALUE) return new IntegerRange(a.getMin(), a.getMax() - 1);
        // TODO: If we add a opposite of range (i.e. everything except for X), could do more handling here
        return a;
    }

    @Override
    public PossibleValues visit(NullValue a, ObjectValue b) {
        if (b.equals(a)) return new EmptyValue();
        return a;
    }

    @Override
    public PossibleValues visit(ObjectValue a, NullValue b) {
        if (a.equals(b)) return new EmptyValue();
        return a.withNotNullable();
    }
}
