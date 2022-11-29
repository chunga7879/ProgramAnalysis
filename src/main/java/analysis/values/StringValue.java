package analysis.values;

import analysis.values.visitor.OperationVisitor;

public class StringValue extends ObjectWithNotNullValue {
    public static final StringValue ANY_VALUE = new StringValue(0, Integer.MAX_VALUE, true);

    private final int min;
    private final int max;

    public StringValue(String s) {
        super(false);
        this.min = s.length();
        this.max = s.length();
    }

    public StringValue(int min, int max) {
        this(min, max, false);
    }

    public StringValue(int min, int max, boolean canBeNull) {
        super(canBeNull);
        assert min <= max;
        assert min >= 0;
        this.min = min;
        this.max = max;
    }

    @Override
    public int minStringLength() {
        return this.min;
    }

    @Override
    public int maxStringLength() {
        return this.max;
    }

    @Override
    public <T> T acceptAbstractOp(OperationVisitor<T> visitor, PossibleValues b) {
        return visitor.visitAbstract(this, b);
    }

    @Override
    public <T> T acceptOp(OperationVisitor<T> visitor, PossibleValues a) {
        return visitor.visit(a, this);
    }

    @Override
    public <T> T acceptOp(OperationVisitor<T> visitor, AnyValue a) {
        return visitor.visit(a, this);
    }

    @Override
    public <T> T acceptOp(OperationVisitor<T> visitor, IntegerValue a) {
        return visitor.visit(a, this);
    }

    @Override
    public <T> T acceptOp(OperationVisitor<T> visitor, StringValue a) {
        return visitor.visit(a, this);
    }

    @Override
    public <T> T acceptOp(OperationVisitor<T> visitor, NullValue a) {
        return visitor.visit(a, (ObjectValue) this);
    }

    @Override
    public <T> T acceptOp(OperationVisitor<T> visitor, BooleanValue a) {
        return visitor.visit(a, this);
    }

    @Override
    public ObjectWithNotNullValue copy() {
        return new StringValue(this.min, this.max, this.canBeNull());
    }

    @Override
    public String toFormattedString() {
        String len = "[" + (this.min == this.max ? this.min : (this.min + "," + this.max)) + "]";
        return this.canBeNull() ? "{null, String" + len + "}" : "{String" + len + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        StringValue that = (StringValue) o;

        if (min != that.min) return false;
        return max == that.max;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + min;
        result = 31 * result + max;
        return result;
    }
}
