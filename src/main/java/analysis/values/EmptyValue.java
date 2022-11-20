package analysis.values;

import analysis.values.visitor.OperationVisitor;

/**
 * Empty domain
 */
public final class EmptyValue extends PossibleValues {
    @Override
    public <T> T acceptAbstractOp(OperationVisitor<T> visitor, PossibleValues b) {
        return visitor.visitAbstract(this, b);
    }

    @Override
    public <T> T acceptOp(OperationVisitor<T> visitor, PossibleValues a) {
        return visitor.visit((PossibleValues) a, this);
    }

    @Override
    public <T> T acceptOp(OperationVisitor<T> visitor, AnyValue a) {
        return visitor.visit((PossibleValues) a, this);
    }

    @Override
    public <T> T acceptOp(OperationVisitor<T> visitor, IntegerValue a) {
        return visitor.visit((PossibleValues) a, this);
    }

    @Override
    public <T> T acceptOp(OperationVisitor<T> visitor, StringValue a) {
        return visitor.visit((PossibleValues) a, this);
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean canBeNull() {
        return false;
    }

    @Override
    public String toFormattedString() {
        return "{empty}";
    }
}
