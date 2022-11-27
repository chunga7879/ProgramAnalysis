package analysis.values;

import analysis.values.visitor.OperationVisitor;

public class CharValue extends PrimitiveValue {
    public static final CharValue ANY_VALUE = new CharValue(Character.MIN_VALUE, Character.MAX_VALUE);
    private final int min;
    private final int max;

    public CharValue(char c) {
        this.min = c;
        this.max = c;
    }

    public CharValue(char min, char max) {
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

    @Override
    public <T> T acceptAbstractOp(OperationVisitor<T> visitor, PossibleValues b) {
        return visitor.visitAbstract(this, b);
    }

    @Override
    public <T> T acceptOp(OperationVisitor<T> visitor, CharValue a) {
        return visitor.visit(a, this);
    }

    @Override
    public <T> T acceptOp(OperationVisitor<T> visitor, IntegerValue a) {
        return visitor.visit(a, this);
    }

    @Override
    public int minStringLength() {
        return 1;
    }

    @Override
    public int maxStringLength() {
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CharValue charValue = (CharValue) o;

        if (min != charValue.min) return false;
        return max == charValue.max;
    }

    @Override
    public int hashCode() {
        int result = min;
        result = 31 * result + (int) max;
        return result;
    }

    @Override
    public String toFormattedString() {
        return "[" + (this.min == this.max ? this.min : (this.min + "," + this.max)) + "]";
    }
}
