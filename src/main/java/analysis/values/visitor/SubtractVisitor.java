package analysis.values.visitor;

import analysis.values.IntegerRange;
import analysis.values.PossibleValues;
import utils.MathUtil;

/**
 * Visitor getting possible values for subtraction operation: a - b
 */
public class SubtractVisitor extends OperationVisitorWithDefault {
    @Override
    public PossibleValues visit(IntegerRange a, IntegerRange b) {
        return new IntegerRange(
                MathUtil.subtractToLimit(a.getMin(), b.getMax()),
                MathUtil.subtractToLimit(a.getMax(), b.getMin())
        );
    }
}
