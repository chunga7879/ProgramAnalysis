package analysis.values.visitor;

import analysis.values.*;

/**
 * Visitor for getting possible values after merging operation of a & b
 */
public class MergeVisitor extends OperationVisitorWithDefault {
    @Override
    public IntegerValue visit(IntegerValue a, IntegerValue b) {
        return new IntegerRange(
                Math.min(a.getMin(), b.getMin()),
                Math.max(a.getMax(), b.getMax())
        );
    }

    @Override
    public PossibleValues visit(StringValue a, StringValue b) {
        // TODO: implement merge
        return new StringValue();
    }

    @Override
    public PossibleValues visit(EmptyValue a, PossibleValues b) {
        return b;
    }

    @Override
    public PossibleValues visit(PossibleValues a, EmptyValue b) {
        return a;
    }

    @Override
    public PossibleValues visit(NullValue a, ObjectValue b) {
        return b.withNullable();
    }

    @Override
    public PossibleValues visit(ObjectValue a, NullValue b) {
        return a.withNullable();
    }

    @Override
    public PossibleValues visit(ArrayValue a, ArrayValue b) {
        IntegerValue length = visit(a.getLength(), b.getLength());
        return new ArrayValue(length, canBeNull(a, b));
    }

    /**
     * Whether a merge of a & b can be null
     */
    private boolean canBeNull(ObjectValue a, ObjectValue b) {
        return a.canBeNull() || b.canBeNull();
    }
}
