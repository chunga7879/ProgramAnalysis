package analysis.values.visitor;

import analysis.values.*;
import utils.MathUtil;

/**
 * Visitor getting possible values for multiplication operation: a * b
 */
public class MultiplyVisitor extends OperationVisitorWithDefault {
    @Override
    public PossibleValues visit(IntegerValue a, IntegerValue b) {
        int aMin = a.getMin();
        int aMax = a.getMax();
        int bMin = b.getMin();
        int bMax = b.getMax();

        int product1 = MathUtil.multiplyToLimit(aMin, bMin);
        int product2 = MathUtil.multiplyToLimit(aMin, bMax);
        int product3 = MathUtil.multiplyToLimit(aMax, bMin);
        int product4 = MathUtil.multiplyToLimit(aMax, bMax);

        int newMin = Math.min(Math.min(product1, product2), Math.min(product3, product4));
        int newMax = Math.max(Math.max(product1, product2), Math.max(product3, product4));

        return new IntegerRange(newMin, newMax);
    }

    @Override
    public PossibleValues visit(BoxedPrimitive a, BoxedPrimitive b) {
        return new BoxedPrimitive((PrimitiveValue) a.unbox().acceptAbstractOp(this, b.unbox()));
    }
}
