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
    public PossibleValues visitAbstract(CharValue a, PossibleValues b) {
        return b.acceptOp(this, a);
    }

    @Override
    public PossibleValues visitAbstract(BooleanValue a, PossibleValues b) {
        return b.acceptOp(this, a);
    }

    @Override
    public PossibleValues visitAbstract(ArrayValue a, PossibleValues b) {
        return b.acceptOp(this, a);
    }

    @Override
    public PossibleValues visitAbstract(BoxedPrimitive a, PossibleValues b) {
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
    public PossibleValues visit(NullValue a, ObjectValue b) {
        return new EmptyValue();
    }

    @Override
    public PossibleValues visit(ObjectValue a, NullValue b) {
        return new EmptyValue();
    }

    @Override
    public PossibleValues visit(CharValue a, CharValue b) {
        return a;
    }

    @Override
    public PossibleValues visit(CharValue a, IntegerValue b) {
        return a;
    }

    @Override
    public PossibleValues visit(IntegerValue a, CharValue b) {
        return a;
    }

    @Override
    public PossibleValues visit(BooleanValue a, BooleanValue b) {
        return a;
    }

    @Override
    public PossibleValues visit(ArrayValue a, ArrayValue b) {
        return a;
    }

    @Override
    public PossibleValues visit(BoxedPrimitive a, BoxedPrimitive b) {
        return BoxedPrimitive.create(a.unbox().acceptAbstractOp(this, b.unbox()), false);
    }

    @Override
    public PossibleValues visit(BoxedPrimitive a, PrimitiveValue b) {
        return BoxedPrimitive.create(a.unbox().acceptAbstractOp(this, b), false);
    }

    @Override
    public PossibleValues visit(PrimitiveValue a, BoxedPrimitive b) {
        return a.acceptAbstractOp(this, b.unbox());
    }

    @Override
    public PossibleValues visit(NullValue a, PossibleValues b) {
        return new EmptyValue();
    }

    @Override
    public PossibleValues visit(PossibleValues a, NullValue b) {
        return new EmptyValue();
    }
}
