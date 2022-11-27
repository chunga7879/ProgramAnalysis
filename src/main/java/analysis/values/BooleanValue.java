package analysis.values;


import analysis.values.visitor.OperationVisitor;

public class BooleanValue extends PrimitiveValue {
    private final boolean canBeFalse;
    private final boolean canBeTrue;

    public BooleanValue(boolean b) {
        this.canBeTrue = b;
        this.canBeFalse = !b;
    }

    public BooleanValue(boolean t, boolean f) {
        assert t || f;
        this.canBeTrue = t;
        this.canBeFalse = f;
    }

    public boolean canBeFalse() {
        return canBeFalse;
    }

    public boolean canBeTrue() {
        return canBeTrue;
    }

    @Override
    public String toFormattedString() {
        if (this.canBeTrue && !this.canBeFalse) {
            return "[true]";
        }
        if (!this.canBeTrue && this.canBeFalse) {
            return "[false]";
        }
        return "[true,false]";
    }


    @Override
    public <T> T acceptAbstractOp(OperationVisitor<T> visitor, PossibleValues b) {
        return visitor.visitAbstract(this, b);
    }

    @Override
    public <T> T acceptOp(OperationVisitor<T> visitor, StringValue a) {
        return visitor.visit(a, this);
    }

    @Override
    public <T> T acceptOp(OperationVisitor<T> visitor, BooleanValue a) {
        return visitor.visit(a, this);
    }

    @Override
    public int minStringLength() {
        return this.canBeTrue ? 4 : 5;
    }

    @Override
    public int maxStringLength() {
        return this.canBeFalse ? 5 : 4;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BooleanValue that = (BooleanValue) o;

        if (canBeFalse != that.canBeFalse) return false;
        return canBeTrue == that.canBeTrue;
    }

    @Override
    public int hashCode() {
        int result = (canBeFalse ? 1 : 0);
        result = 31 * result + (canBeTrue ? 1 : 0);
        return result;
    }
}
