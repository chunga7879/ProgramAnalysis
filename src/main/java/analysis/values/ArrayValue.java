package analysis.values;

import analysis.values.visitor.OperationVisitor;

public class ArrayValue extends ObjectWithNotNullValue {
    public static final int MIN_LENGTH_NUM = 0;
    public static final int MAX_LENGTH_NUM = Integer.MAX_VALUE;
    public static final IntegerValue DEFAULT_LENGTH = new IntegerRange(MIN_LENGTH_NUM, MAX_LENGTH_NUM);

    private final IntegerValue length;

    public ArrayValue() {
        this(DEFAULT_LENGTH);
    }

    public ArrayValue(IntegerValue length) {
        this(length, false);
    }

    public ArrayValue(IntegerValue length, boolean canBeNull) {
        super(canBeNull);
        this.length = length == null ? DEFAULT_LENGTH : length;
    }

    @Override
    public <T> T acceptOp(OperationVisitor<T> visitor, PossibleValues a) {
        return visitor.visit(a, this);
    }

    @Override
    public <T> T acceptOp(OperationVisitor<T> visitor, AnyValue a) {
        return visitor.visit(a, this);
    }

    public IntegerValue getLength() {
        return length;
    }

    @Override
    public ObjectWithNotNullValue copy() {
        return new ArrayValue(this.length);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArrayValue that)) return false;

        return canBeNull() == that.canBeNull() && length.equals(that.length);
    }

    @Override
    public int hashCode() {
        return length.hashCode();
    }

    @Override
    public String toFormattedString() {
        return "array(" + length.toFormattedString() + ")" + (this.canBeNull() ? "?" : "" );
    }
}
