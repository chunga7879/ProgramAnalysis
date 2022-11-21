package analysis.values.visitor;

import analysis.values.*;

public class RestrictEqualsVisitor extends RestrictionVisitor {
    @Override
    public PossibleValues visit(IntegerValue a, IntegerValue b) {
        if (a.getMin() > b.getMax()) return new EmptyValue();
        if (a.getMax() < b.getMin()) return new EmptyValue();
        return new IntegerRange(Integer.max(a.getMin(), b.getMin()), Integer.min(a.getMax(), b.getMax()));
    }

    // TODO: handle equals for different types
    @Override
    public PossibleValues visit(PossibleValues a, StringValue b) {
        if (a instanceof IntegerValue) return new EmptyValue(); // handle this better
        return a;
    }

    @Override
    public PossibleValues visit(StringValue a, PossibleValues b) {
        if (b instanceof IntegerValue) return new EmptyValue(); // handle this better
        return a;
    }
}
