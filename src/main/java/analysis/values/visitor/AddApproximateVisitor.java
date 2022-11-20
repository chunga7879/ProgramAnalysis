package analysis.values.visitor;

import analysis.values.IntegerRange;
import analysis.values.IntegerValue;
import analysis.values.PossibleValues;

public class AddApproximateVisitor extends AddVisitor {
    @Override
    public PossibleValues visit(IntegerValue a, IntegerValue b) {
        if (a.getMin() >= 0 && b.getMin() >= 0) {
            return new IntegerRange(Math.min(a.getMin(), b.getMin()), Integer.MAX_VALUE);
        } else if (a.getMax() <= 0 && b.getMax() <= 0) {
            return new IntegerRange(Integer.MIN_VALUE, Math.max(a.getMax(), b.getMax()));
        }
        return new IntegerRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
}
