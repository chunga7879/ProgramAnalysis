package analysis.values.visitor;

import analysis.values.*;

public class RestrictNotEqualsVisitor extends RestrictionVisitor {
    @Override
    public PossibleValues visit(IntegerValue a, IntegerValue b) {
        if (b.getMin() != b.getMax()) return a;
        if (b.getMin() == a.getMin() && b.getMin() == a.getMax()) return new EmptyValue();
        if (b.getMin() == a.getMin() && a.getMin() != Integer.MAX_VALUE) return new IntegerRange(a.getMin() + 1, a.getMax());
        if (b.getMin() == a.getMax() && a.getMax() != Integer.MIN_VALUE) return new IntegerRange(a.getMin(), a.getMax() - 1);
        // TODO: If we add a opposite of range (i.e. everything except for X), could do more handling here
        return a;
    }


    @Override
    public PossibleValues visit(BooleanValue a, BooleanValue b) {
        if (b.canBeTrue() && b.canBeFalse()) return a;
        if (a.canBeTrue() && b.canBeFalse()) return BooleanValue.TRUE;
        if (a.canBeFalse() && b.canBeTrue()) return BooleanValue.FALSE;
        return EmptyValue.VALUE;
    }

    @Override
    public PossibleValues visit(CharValue a, CharValue b) {
        if (b.getMin() != b.getMax()) return a;
        if (b.getMin() == a.getMin() && b.getMin() == a.getMax()) return new EmptyValue();
        if (b.getMin() == a.getMin() && a.getMin() != Character.MAX_VALUE) return new CharValue((char) (a.getMin() + 1), (char) a.getMax());
        if (b.getMin() == a.getMax() && a.getMax() != Character.MIN_VALUE) return new CharValue((char) a.getMin(), (char) (a.getMax() - 1));
        return a;
    }

    @Override
    public PossibleValues visit(NullValue a, ObjectValue b) {
        if (NullValue.VALUE == b) return new EmptyValue();
        return a;
    }

    @Override
    public PossibleValues visit(ObjectValue a, NullValue b) {
        if (a == NullValue.VALUE) return new EmptyValue();
        return a.withNotNullable();
    }

    @Override
    public PossibleValues visit(ArrayValue a, ArrayValue b) {
        // To do this properly, you'd need to keep track of potential pointer values
        return a;
    }

    @Override
    public PossibleValues visit(NullValue a, PossibleValues b) {
        if (NullValue.VALUE == b) return new EmptyValue();
        return a;
    }

    @Override
    public PossibleValues visit(PossibleValues a, NullValue b) {
        if (NullValue.VALUE == a) return new EmptyValue();
        if (a.canBeNull() && a instanceof ObjectValue objectValue) {
            return objectValue.withNotNullable();
        }
        return a;
    }

    @Override
    public PossibleValues visit(BoxedPrimitive a, BoxedPrimitive b) {
        return BoxedPrimitive.create(a.unbox().acceptAbstractOp(this, b.unbox()), a.canBeNull());
    }
}
