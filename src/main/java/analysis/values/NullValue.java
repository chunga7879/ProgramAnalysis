package analysis.values;

import analysis.values.visitor.OperationVisitor;

/**
 * Value that is null
 */
public class NullValue extends ObjectValue {
    public static final NullValue VALUE = new NullValue();

    private NullValue() {
    }

    @Override
    public <T> T acceptAbstractOp(OperationVisitor<T> visitor, PossibleValues b) {
        return visitor.visitAbstract(this, b);
    }

    @Override
    public <T> T acceptOp(OperationVisitor<T> visitor, ObjectValue a) {
        return visitor.visit(a, this);
    }

    @Override
    public <T> T acceptOp(OperationVisitor<T> visitor, NullValue a) {
        return visitor.visit(a, (ObjectValue) this);
    }

    @Override
    public ObjectValue withNullable() {
        return this;
    }

    @Override
    public EmptyValue withNotNullable() {
        return new EmptyValue();
    }

    @Override
    public boolean canBeNull() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o instanceof NullValue;
    }

    @Override
    public int hashCode() {
        return 31 * getClass().hashCode();
    }
}
