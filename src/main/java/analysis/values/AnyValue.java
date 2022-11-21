package analysis.values;

import analysis.values.visitor.OperationVisitor;

public class AnyValue extends PossibleValues {
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
    public String toFormattedString() {
        return "{any}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o instanceof AnyValue;
    }

    @Override
    public int hashCode() {
        return 31 * getClass().hashCode();
    }
}
