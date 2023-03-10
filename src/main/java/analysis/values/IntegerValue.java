package analysis.values;

import analysis.values.visitor.OperationVisitor;

public abstract class IntegerValue extends PrimitiveValue {
    public abstract int getMin();
    public abstract int getMax();

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
    public int minStringLength() {
        return String.valueOf(this.getMin()).length();
    }

    @Override
    public int maxStringLength() {
        return String.valueOf(this.getMax()).length();
    }
}
