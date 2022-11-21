package analysis.values;

import analysis.values.visitor.OperationVisitor;

/**
 * Values that are objects (pointers)
 */
public abstract class ObjectValue extends PossibleValues {

    @Override
    public <T> T acceptAbstractOp(OperationVisitor<T> visitor, PossibleValues b) {
        return visitor.visitAbstract(this, b);
    }

    @Override
    public <T> T acceptOp(OperationVisitor<T> visitor, ObjectValue a) {
        return visitor.visit(a, this);
    }

    /**
     * Create an object value that is this value but nullable
     */
    public abstract ObjectValue withNullable();

    @Override
    public abstract boolean canBeNull();
}
