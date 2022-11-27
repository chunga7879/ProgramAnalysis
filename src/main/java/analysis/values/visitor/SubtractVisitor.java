package analysis.values.visitor;

import analysis.values.CharValue;
import analysis.values.IntegerRange;
import analysis.values.IntegerValue;
import analysis.values.PossibleValues;
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
}
