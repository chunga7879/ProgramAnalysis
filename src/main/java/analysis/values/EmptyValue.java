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
    public boolean isEmpty() {
        return true;
    }

    @Override
    public String toFormattedString() {
        return "{empty}";
    }
}
