package analysis.values;

import analysis.values.visitor.OperationVisitor;

import java.util.Objects;

public class BoxedPrimitive extends ObjectWithNotNullValue {

    private final PrimitiveValue v;

    public BoxedPrimitive(PrimitiveValue v) {
        this(v, false);
    }

    public BoxedPrimitive(PrimitiveValue v, boolean canBeNull) {
        super(canBeNull);
        this.v = v;
    }

    public static PossibleValues create(PossibleValues inner, boolean canBeNull) {
        if (inner.isEmpty()) return new EmptyValue();
        if (inner instanceof PrimitiveValue primitiveInner) {
            return new BoxedPrimitive(primitiveInner, canBeNull);
        }
        return new AnyValue();
    }

    public PrimitiveValue unbox() {
        return this.v;
    }

    @Override
    public <T> T acceptAbstractOp(OperationVisitor<T> visitor, PossibleValues b) {
        return visitor.visitAbstract(this, b);
    }

    @Override
    public <T> T acceptOp(OperationVisitor<T> visitor, BoxedPrimitive a) {
        return visitor.visit(a, this);
    }

    @Override
    public <T> T acceptOp(OperationVisitor<T> visitor, IntegerValue a) {
        return visitor.visit(a, this);
    }

    @Override
    public <T> T acceptOp(OperationVisitor<T> visitor, CharValue a) {
        return visitor.visit(a, this);
    }

    @Override
    public <T> T acceptOp(OperationVisitor<T> visitor, BooleanValue a) {
        return visitor.visit(a, this);
    }

    @Override
    public <T> T acceptOp(OperationVisitor<T> visitor, NullValue a) {
        return visitor.visit(a, this);
    }

    @Override
    public ObjectWithNotNullValue copy() {
        return new BoxedPrimitive(this.v);
    }

    @Override
    public String toFormattedString() {
        return "{" + (this.canBeNull() ? "null, " : "" ) + this.v.toFormattedString() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        BoxedPrimitive that = (BoxedPrimitive) o;

        return Objects.equals(v, that.v);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (v != null ? v.hashCode() : 0);
        return result;
    }
}
