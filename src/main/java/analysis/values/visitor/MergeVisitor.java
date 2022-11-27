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
    public PossibleValues visit(CharValue a, CharValue b) {
        return new IntegerRange(
                Math.min(a.getMin(), b.getMin()),
                Math.max(a.getMax(), b.getMax())
        );
    }

    @Override
    public PossibleValues visit(StringValue a, StringValue b) {
        return new StringValue(
                Math.min(a.minStringLength(), b.minStringLength()),
                Math.max(a.maxStringLength(), b.maxStringLength())
        );
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
    public PossibleValues visit(BooleanValue a, BooleanValue b) {
        return new BooleanValue(a.canBeTrue() || b.canBeTrue(), a.canBeFalse() || b.canBeFalse());
    }

    @Override
    public PossibleValues visit(ArrayValue a, ArrayValue b) {
        IntegerValue length = visit(a.getLength(), b.getLength());
        return new ArrayValue(length, canBeNull(a, b));
    }

    @Override
    public PossibleValues visit(BoxedPrimitive a, BoxedPrimitive b) {
        return new BoxedPrimitive((PrimitiveValue) a.unbox().acceptAbstractOp(this, b.unbox()));
    }

    /**
     * Whether a merge of a & b can be null
     */
    private boolean canBeNull(ObjectValue a, ObjectValue b) {
        return a.canBeNull() || b.canBeNull();
    }
}
