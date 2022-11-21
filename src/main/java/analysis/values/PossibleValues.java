package analysis.values;

import analysis.values.visitor.OperationVisitor;

public abstract class PossibleValues implements ValueVisitable {
    public <T> T acceptAbstractOp(OperationVisitor<T> visitor, PossibleValues b) {
        return visitor.visitAbstract(this, b);
    }
    public <T> T acceptOp(OperationVisitor<T> visitor, PossibleValues a) {
        return visitor.visit(a, this);
    }

    public <T> T acceptOp(OperationVisitor<T> visitor, AnyValue a) {
        return visitor.visit(a, this);
    }

    public final <T> T acceptOp(OperationVisitor<T> visitor, EmptyValue a) {
        return visitor.visit(a, this);
    }

    public <T> T acceptOp(OperationVisitor<T> visitor, IntegerValue a) {
        return visitor.visit(a, this);
    }
    public <T> T acceptOp(OperationVisitor<T> visitor, StringValue a) {
        return visitor.visit(a, this);
    }

    public <T> T acceptOp(OperationVisitor<T> visitor, ObjectValues a) {
        return visitor.visit(a, this);
    }

    public <T> T acceptOp(OperationVisitor<T> visitor, NullValue a) {
        return visitor.visit(a, this);
    }

    /**
     * Is the domain empty?
     */
    public boolean isEmpty() {
        return false;
    }

    public abstract boolean canBeNull();

    @Override
    public String toString() {
        return toFormattedString();
    }

    /**
     * Get a formatted string describing the domain
     */
    public String toFormattedString() {
        return "{domain}";
    }
}
