package analysis.values;

import analysis.values.visitor.OperationVisitor;

import java.util.Objects;

public class BoxedPrimitive<V extends PrimitiveValue> extends ObjectWithNotNullValue {

    private final V v;

    public BoxedPrimitive(V v) {
        this.v = v;
    }

    public V unbox() {
        return this.v;
    }

    @Override
    public <T> T acceptAbstractOp(OperationVisitor<T> visitor, PossibleValues b) {
        return visitor.visitAbstract(this, b);
    }

    @Override
    public <T, S extends PrimitiveValue> T acceptOp(OperationVisitor<T> visitor, BoxedPrimitive<S> a) {
        return visitor.visit(a, this);
    }

    @Override
    public ObjectWithNotNullValue copy() {
        return new BoxedPrimitive<>(v);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        BoxedPrimitive<?> that = (BoxedPrimitive<?>) o;

        return Objects.equals(v, that.v);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (v != null ? v.hashCode() : 0);
        return result;
    }
}
