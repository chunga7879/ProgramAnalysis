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

    public <T> T acceptOp(OperationVisitor<T> visitor, IntegerRange a) {
        return visitor.visit(a, this);
    }
    public <T> T acceptOp(OperationVisitor<T> visitor, StringValue a) {
        return visitor.visit(a, this);
    }
}
