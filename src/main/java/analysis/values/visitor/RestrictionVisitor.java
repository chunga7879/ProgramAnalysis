package analysis.values.visitor;

import analysis.values.*;

public abstract class RestrictionVisitor implements OperationVisitor<PossibleValues> {
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
    public abstract PossibleValues visit(IntegerValue a, IntegerValue b);

    @Override
    public PossibleValues visit(IntegerValue a, AnyValue b) {
        return a;
    }

    @Override
    public PossibleValues visit(AnyValue a, IntegerValue b) {
        return visit(new IntegerRange(Integer.MIN_VALUE, Integer.MAX_VALUE), b);
    }

    @Override
    public PossibleValues visit(PossibleValues a, PossibleValues b) {
        return a;
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
    public PossibleValues visit(StringValue a, StringValue b) {
        return a;
    }

    @Override
    public PossibleValues visit(PossibleValues a, StringValue b) {
        return a;
    }

    @Override
    public PossibleValues visit(StringValue a, PossibleValues b) {
        return a;
    }

    @Override
    public PossibleValues visit(NullValue a, ObjectValue b) {
        return new EmptyValue();
    }

    @Override
    public PossibleValues visit(ObjectValue a, NullValue b) {
        return new EmptyValue();
    }
}
