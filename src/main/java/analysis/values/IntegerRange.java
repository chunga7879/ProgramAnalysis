package analysis.values;

import analysis.values.visitor.OperationVisitor;

/**
 * Represents an integer range between min and max (inclusive)
 */
public class IntegerRange extends IntegerValue {
    public static final IntegerRange ANY_VALUE = new IntegerRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
    private final int min;
    private final int max;

    public IntegerRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public IntegerRange(int val) {
        this(val, val);
    }

    @Override
    public int getMin() {
        return min;
    }

    @Override
    public int getMax() {
        return max;
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
    public <T> T acceptOp(OperationVisitor<T> visitor, CharValue a) {
        return visitor.visit(a, this);
    }

    @Override
    public String toFormattedString() {
        return "[" + (min == max ? min : (min + "," + max)) + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntegerRange that)) return false;

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
