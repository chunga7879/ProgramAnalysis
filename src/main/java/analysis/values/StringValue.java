package analysis.values;

import analysis.values.visitor.OperationVisitor;

public class StringValue extends ObjectWithNotNullValue {
    private final int min;
    private final int max;

    public StringValue(String s) {
        this.min = s.length();
        this.max = s.length();
    }

    public StringValue(int min, int max) {
        assert min <= max;
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
    public ObjectWithNotNullValue copy() {
        return new StringValue(this.min, this.max);
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

        StringValue that = (StringValue) o;

        if (min != that.min) return false;
        return max == that.max;
    }

    @Override
    public int hashCode() {
        int result = min;
        result = 31 * result + max;
        return result;
    }
}
