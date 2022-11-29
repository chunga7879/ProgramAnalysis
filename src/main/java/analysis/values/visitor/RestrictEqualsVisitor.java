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

    @Override
    public PossibleValues visit(StringValue a, StringValue b) {
        if (a.minStringLength() > b.maxStringLength()) return new EmptyValue();
        if (a.maxStringLength() < b.minStringLength()) return new EmptyValue();
        return new StringValue(
                Integer.max(a.minStringLength(), b.minStringLength()),
                Integer.min(a.maxStringLength(), b.maxStringLength()),
                a.canBeNull() && b.canBeNull()
        );
    }

    @Override
    public PossibleValues visit(CharValue a, CharValue b) {
        if (a.getMin() > b.getMax()) return new EmptyValue();
        if (a.getMax() < b.getMin()) return new EmptyValue();
        return new CharValue((char) Integer.max(a.getMin(), b.getMin()), (char) Integer.min(a.getMax(), b.getMax()));
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
