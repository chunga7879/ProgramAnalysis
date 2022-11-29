package analysis.values.visitor;

import analysis.values.*;

import java.util.Objects;

public class RestrictEqualsVisitor extends RestrictionVisitor {
    @Override
    public PossibleValues visit(IntegerValue a, IntegerValue b) {
        if (a.getMin() > b.getMax()) return new EmptyValue();
        if (a.getMax() < b.getMin()) return new EmptyValue();
        return new IntegerRange(Integer.max(a.getMin(), b.getMin()), Integer.min(a.getMax(), b.getMax()));
    }

    @Override
    public PossibleValues visit(BooleanValue a, BooleanValue b) {
        if (Objects.equals(a, b)) return a;
        if (a.canBeTrue() && a.canBeFalse()) return b;
        if (a.canBeTrue() && b.canBeTrue()) return a;
        if (a.canBeFalse() && b.canBeFalse()) return a;
        return new EmptyValue();
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

    @Override
    public PossibleValues visit(NullValue a, ObjectValue b) {
        if (b.canBeNull()) return a;
        return new EmptyValue();
    }

    @Override
    public PossibleValues visit(ObjectValue a, NullValue b) {
        if (a.canBeNull()) return b;
        return new EmptyValue();
    }

    @Override
    public PossibleValues visit(ArrayValue a, ArrayValue b) {
        // To do this properly, you'd need to keep track of potential pointer values
        return a;
    }
}
