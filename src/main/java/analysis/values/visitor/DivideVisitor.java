package analysis.values.visitor;

import analysis.values.EmptyValue;
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
        int aMin = a.getMin();
        int aMax = a.getMax();
        int bMin = b.getMin();
        int bMax = b.getMax();

        // 0 is the denominator
        if (bMin == 0 && bMax == 0) {
            // TODO: add error to analysis state
            return new EmptyValue();
        }

        if (bMin == 0) {
            // TODO: fix
            bMin = 1;
        }

        if (bMax == 0) {
            // TODO: fix
            bMax = 1;
        }

        // 0 is a possible value for the denominator
        if (bMin < 0 && bMax > 0) {
            // TODO: implement
        }

        int quotient1 = MathUtil.divideToLimit(aMin, bMin);
        int quotient2 = MathUtil.divideToLimit(aMin, bMax);
        int quotient3 = MathUtil.divideToLimit(aMax, bMin);
        int quotient4 = MathUtil.divideToLimit(aMax, bMax);

        int newMin = Math.min(Math.min(quotient1, quotient2), Math.min(quotient3, quotient4));
        int newMax = Math.max(Math.max(quotient1, quotient2), Math.max(quotient3, quotient4));

        return new IntegerRange(newMin, newMax);
    }
}
