package analysis.values.visitor;

import analysis.values.*;
import utils.MathUtil;

/**
 * Visitor getting possible values for addition operation: a + b
 */
public class AddVisitor extends OperationVisitorWithDefault {
    @Override
    public PossibleValues visit(IntegerValue a, IntegerValue b) {
        return new IntegerRange(
                MathUtil.addToLimit(a.getMin(), b.getMin()),
                MathUtil.addToLimit(a.getMax(), b.getMax())
        );
    }

    @Override
    public PossibleValues visit(StringValue a, StringValue b) {
        return this.visit(a, (PossibleValues) b);
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
        return new IntegerRange(
                MathUtil.addToLimit(a.getMin(), b.getMin()),
                MathUtil.addToLimit(a.getMax(), b.getMax())
        );
    }

    @Override
    public PossibleValues visit(CharValue a, IntegerValue b) {
        return new IntegerRange(
                MathUtil.addToLimit(a.getMin(), b.getMin()),
                MathUtil.addToLimit(a.getMax(), b.getMax())
        );
    }

    @Override
    public PossibleValues visit(IntegerValue a, CharValue b) {
        return this.visit(b, a);
    }

    @Override
    public PossibleValues visit(BoxedPrimitive a, BoxedPrimitive b) {
        return new BoxedPrimitive((PrimitiveValue) a.unbox().acceptAbstractOp(this, b.unbox()));
    }
}
