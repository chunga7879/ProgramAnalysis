package analysis.values;

import analysis.values.visitor.OperationVisitor;

/**
 * Values that are objects (pointers)
 */
public abstract class ObjectValues extends PossibleValues {
    public abstract ObjectValues withNullable();

    @Override
    public <T> T acceptAbstractOp(OperationVisitor<T> visitor, PossibleValues b) {
        return visitor.visitAbstract(this, b);
    }

    @Override
    public <T> T acceptOp(OperationVisitor<T> visitor, ObjectValues a) {
        return visitor.visit(a, this);
    }

    @Override
    public abstract boolean canBeNull();
}
