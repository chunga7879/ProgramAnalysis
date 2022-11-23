package analysis.values.visitor;

import analysis.values.*;

public class IntersectVisitor extends OperationVisitorWithDefault {
    @Override
    public PossibleValues visit(IntegerValue a, IntegerValue b) {
        if (a.getMin() > b.getMax()) return new EmptyValue();
        if (a.getMax() < b.getMin()) return new EmptyValue();
        return new IntegerRange(
                Math.max(a.getMin(), b.getMin()),
                Math.min(a.getMax(), b.getMax())
        );
    }

    // TODO: implement
    @Override
    public PossibleValues visit(NullValue a, ObjectValue b) {
        if (!b.canBeNull()) return new EmptyValue();
        return a;
    }

    @Override
    public PossibleValues visit(ObjectValue a, NullValue b) {
        if (!a.canBeNull()) return new EmptyValue();
        return b;
    }

    @Override
    public PossibleValues visit(ArrayValue a, ArrayValue b) {
        PossibleValues length = visit(a.getLength(), b.getLength());
        if (length instanceof IntegerValue intLength) {
            return withNullableSet(new ArrayValue(intLength), a, b);
        } else if (length instanceof EmptyValue) {
            return new EmptyValue();
        }
        return withNullableSet(new ArrayValue(), a, b);
    }

    PossibleValues withNullableSet(ObjectValue val, ObjectValue a, ObjectValue b) {
        return (a.canBeNull() && b.canBeNull()) ? val.withNullable() : val.withNotNullable();
    }
}
