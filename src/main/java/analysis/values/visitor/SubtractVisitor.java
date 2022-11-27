package analysis.values.visitor;

import analysis.values.*;
import utils.MathUtil;

/**
 * Visitor getting possible values for subtraction operation: a - b
 */
public class SubtractVisitor extends OperationVisitorWithDefault {
    @Override
    public PossibleValues visit(IntegerValue a, IntegerValue b) {
        return new IntegerRange(
                MathUtil.subtractToLimit(a.getMin(), b.getMax()),
                MathUtil.subtractToLimit(a.getMax(), b.getMin())
        );
    }

    @Override
    public PossibleValues visit(CharValue a, CharValue b) {
        return new IntegerRange(
                MathUtil.subtractToLimit(a.getMin(), b.getMax()),
                MathUtil.subtractToLimit(a.getMax(), b.getMin())
        );
    }

    @Override
    public PossibleValues visit(CharValue a, IntegerValue b) {
        return new IntegerRange(
                MathUtil.subtractToLimit(a.getMin(), b.getMax()),
                MathUtil.subtractToLimit(a.getMax(), b.getMin())
        );
    }

    @Override
    public PossibleValues visit(IntegerValue a, CharValue b) {
        return new IntegerRange(
                MathUtil.subtractToLimit(a.getMin(), b.getMax()),
                MathUtil.subtractToLimit(a.getMax(), b.getMin())
        );
    }

    @Override
    public PossibleValues visit(BoxedPrimitive a, BoxedPrimitive b) {
        return new BoxedPrimitive((PrimitiveValue) a.unbox().acceptAbstractOp(this, b.unbox()));
    }
}
