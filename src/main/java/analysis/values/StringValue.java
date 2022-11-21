package analysis.values;

import analysis.values.visitor.OperationVisitor;

public class StringValue extends ObjectWithNotNullValues {

    public StringValue() {
        // TODO: add domain for String
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
    public <T> T acceptOp(OperationVisitor<T> visitor, StringValue a) {
        return visitor.visit(a, this);
    }

    @Override
    public <T> T acceptOp(OperationVisitor<T> visitor, NullValue a) {
        return visitor.visit(a, (ObjectValues) this);
    }

    @Override
    public ObjectWithNotNullValues copy() {
        // TODO: implement
        return new StringValue();
    }
}
