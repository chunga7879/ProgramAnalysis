package analysis.values;

import analysis.values.visitor.OperationVisitor;

/**
 * Values that are objects that can/cannot be null
 */
public abstract class ObjectWithNotNullValues extends ObjectValues {
    private boolean canBeNull;

    public ObjectWithNotNullValues() {
        this.canBeNull = true;
    }

    public ObjectWithNotNullValues(boolean canBeNull) {
        this.canBeNull = canBeNull;
    }

    public ObjectWithNotNullValues withNullable() {
        ObjectWithNotNullValues copy = this.copy();
        copy.canBeNull = true;
        return copy;
    }

    public abstract ObjectWithNotNullValues copy();

    @Override
    public <T> T acceptAbstractOp(OperationVisitor<T> visitor, PossibleValues b) {
        return visitor.visitAbstract(this, b);
    }

    @Override
    public boolean canBeNull() {
        return canBeNull;
    }
}
