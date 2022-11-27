package analysis.values.visitor;

import analysis.model.AnalysisError;
import analysis.model.AnalysisState;
import analysis.model.ExpressionAnalysisState;
import analysis.values.*;
import utils.MathUtil;

/**
 * Visitor getting possible values for division operation: a / b
 */
public class DivideVisitor extends AbstractOperationVisitor {
    @Override
    public PairValue<PossibleValues, AnalysisError> visit(IntegerValue a, IntegerValue b) {
        int aMin = a.getMin();
        int aMax = a.getMax();
        int bMin = b.getMin();
        int bMax = b.getMax();

        // 0 is the denominator
        if (bMin == 0 && bMax == 0) {
            return new PairValue<>(new EmptyValue(),
                    new AnalysisError("ArithmeticException: " + a + " / " + b, true));
        }

        AnalysisError error = null;
        // 0 is a possible value for the denominator
        if (bMin <= 0 && bMax >= 0) {
            error = new AnalysisError("ArithmeticException: " + a + " / " + b);
            // if the minimum denominator is 0, we shift the range from [0, n] to [1, n]
            if (bMin == 0) {
                bMin = 1;
            // if the maximum denominator is 0, we shift the range from [-n, 0] to [-n, -1]
            } else if (bMax == 0) {
                bMax = -1;
            }
        }

        int quotient1 = MathUtil.divideToLimit(aMin, bMin);
        int quotient2 = MathUtil.divideToLimit(aMin, bMax);
        int quotient3 = MathUtil.divideToLimit(aMax, bMin);
        int quotient4 = MathUtil.divideToLimit(aMax, bMax);

        int newMin = Math.min(Math.min(quotient1, quotient2), Math.min(quotient3, quotient4));
        int newMax = Math.max(Math.max(quotient1, quotient2), Math.max(quotient3, quotient4));

        return new PairValue<>(new IntegerRange(newMin, newMax), error);
    }

    @Override
    public PairValue<PossibleValues, AnalysisError> visit(IntegerValue a, AnyValue b) {
        return new PairValue<>(new AnyValue(), new AnalysisError("ArithmeticException: " + a + " / " + b));
    }

}
