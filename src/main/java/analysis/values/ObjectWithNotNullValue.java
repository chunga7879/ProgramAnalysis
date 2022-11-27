package analysis.values;

import analysis.values.visitor.OperationVisitor;

/**
 * Values that are objects that can/cannot be null
 */
public abstract class ObjectWithNotNullValue extends ObjectValue {
    private boolean canBeNull;

    public ObjectWithNotNullValue() {
        this.canBeNull = true;
    }

    public ObjectWithNotNullValue(boolean canBeNull) {
        this.canBeNull = canBeNull;
    }

    @Override
    public ObjectWithNotNullValue withNullable() {
        if (this.canBeNull()) return this;
        ObjectWithNotNullValue copy = this.copy();
        copy.canBeNull = true;
        return copy;
    }

    @Override
    public ObjectWithNotNullValue withNotNullable() {
        if (!this.canBeNull()) return this;
        ObjectWithNotNullValue copy = this.copy();
        copy.canBeNull = false;
        return copy;
    }

    /**
     * Create a copy of this object
     */
    public abstract ObjectWithNotNullValue copy();

    @Override
    public <T> T acceptAbstractOp(OperationVisitor<T> visitor, PossibleValues b) {
        return visitor.visitAbstract(this, b);
    }

    @Override
    public boolean canBeNull() {
        return canBeNull;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjectWithNotNullValue that = (ObjectWithNotNullValue) o;

        return canBeNull == that.canBeNull;
    }

    @Override
    public int hashCode() {
        return (canBeNull ? 1 : 0);
    }
}
