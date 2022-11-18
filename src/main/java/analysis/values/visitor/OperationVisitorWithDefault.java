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
    public PossibleValues visitAbstract(IntegerRange a, PossibleValues b) {
        return b.acceptOp(this, a);
    }

    @Override
    public PossibleValues visitAbstract(StringValue a, PossibleValues b) {
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
    public PossibleValues visit(IntegerRange a, IntegerRange b) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(IntegerRange a, AnyValue b) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(AnyValue a, IntegerRange b) {
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
}
