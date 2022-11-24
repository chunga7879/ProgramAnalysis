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
            return new ArrayValue(intLength, canBeNull(a, b));
        } else if (length instanceof EmptyValue) {
            return new EmptyValue();
        }
        return new ArrayValue(ArrayValue.DEFAULT_LENGTH, canBeNull(a, b));
    }

    /**
     * Whether an intersect of a & b can be null
     */
    private boolean canBeNull(ObjectValue a, ObjectValue b) {
        return a.canBeNull() && b.canBeNull();
    }
}
