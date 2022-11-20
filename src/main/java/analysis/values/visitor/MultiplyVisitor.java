package analysis.values.visitor;

import analysis.values.IntegerRange;
import analysis.values.IntegerValue;
import analysis.values.PossibleValues;
import utils.MathUtil;

/**
 * Visitor getting possible values for multiplication operation: a * b
 */
public class MultiplyVisitor extends OperationVisitorWithDefault {
    @Override
    public PossibleValues visit(IntegerValue a, IntegerValue b) {
        return new IntegerRange(
                MathUtil.multiplyToLimit(a.getMin(), b.getMin()),
                MathUtil.multiplyToLimit(a.getMax(), b.getMax())
        );
    }
}
