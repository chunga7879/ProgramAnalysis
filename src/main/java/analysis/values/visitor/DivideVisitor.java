package analysis.values.visitor;

import analysis.values.IntegerRange;
import analysis.values.IntegerValue;
import analysis.values.PossibleValues;
import utils.MathUtil;

/**
 * Visitor getting possible values for division operation: a / b
 */
public class DivideVisitor extends OperationVisitorWithDefault {
    @Override
    public PossibleValues visit(IntegerValue a, IntegerValue b) {
        if (b.inRange(0)) return null; // TODO: fix me
        return new IntegerRange(
                MathUtil.divideToLimit(a.getMin(), b.getMin()),
                MathUtil.divideToLimit(a.getMax(), b.getMax())
        );
    }
}
