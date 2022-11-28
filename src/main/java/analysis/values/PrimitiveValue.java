package analysis.values;

import analysis.values.visitor.OperationVisitor;

/**
 * Values that are primitives
 */
public abstract class PrimitiveValue extends PossibleValues {
    @Override
    public final boolean canBeNull() {
        return false;
    }

    @Override
    public <T> T acceptAbstractOp(OperationVisitor<T> visitor, PossibleValues b) {
        return visitor.visitAbstract(this, b);
    }

    @Override
    public <T> T acceptOp(OperationVisitor<T> visitor, BoxedPrimitive a) {
        return visitor.visit(a, this);
    }
}
