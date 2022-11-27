package analysis.values.visitor;

import analysis.values.*;

public abstract class OperationVisitorWithDefault implements OperationVisitor<PossibleValues> {
    @Override
    public PossibleValues visitAbstract(PossibleValues a, PossibleValues b) {
        return b.acceptOp(this, a);
    }

    @Override
    public PossibleValues visitAbstract(AnyValue a, PossibleValues b) {
        return b.acceptOp(this, a);
    }

    @Override
    public PossibleValues visitAbstract(EmptyValue a, PossibleValues b) {
        return b.acceptOp(this, a);
    }

    @Override
    public PossibleValues visitAbstract(IntegerValue a, PossibleValues b) {
        return b.acceptOp(this, a);
    }

    @Override
    public PossibleValues visitAbstract(StringValue a, PossibleValues b) {
        return b.acceptOp(this, a);
    }

    @Override
    public PossibleValues visitAbstract(ObjectValue a, PossibleValues b) {
        return b.acceptOp(this, a);
    }

    @Override
    public PossibleValues visitAbstract(NullValue a, PossibleValues b) {
        return b.acceptOp(this, a);
    }

    @Override
    public PossibleValues visitAbstract(CharValue a, PossibleValues b) {
        return b.acceptOp(this, a);
    }

    @Override
    public PossibleValues visit(PossibleValues a, PossibleValues b) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(EmptyValue a, PossibleValues b) {
        return a;
    }

    @Override
    public PossibleValues visit(PossibleValues a, EmptyValue b) {
        return b;
    }

    @Override
    public PossibleValues visit(IntegerValue a, IntegerValue b) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(IntegerValue a, AnyValue b) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(AnyValue a, IntegerValue b) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(StringValue a, StringValue b) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(PossibleValues a, StringValue b) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(StringValue a, PossibleValues b) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(NullValue a, ObjectValue b) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(ObjectValue a, NullValue b) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(CharValue a, CharValue b) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(CharValue a, IntegerValue b) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(IntegerValue a, CharValue b) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(BooleanValue a, BooleanValue b) {
        return new AnyValue();
    }
}
