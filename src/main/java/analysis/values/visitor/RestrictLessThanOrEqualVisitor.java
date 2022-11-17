package analysis.values.visitor;

import analysis.values.EmptyValue;
import analysis.values.IntegerRange;
import analysis.values.PossibleValues;

public class RestrictLessThanOrEqualVisitor extends OperationVisitorWithDefault {
    @Override
    public PossibleValues visit(IntegerRange a, IntegerRange b) {
        if (a.getMin() > b.getMax()) return new EmptyValue();
        return new IntegerRange(a.getMin(), Math.min(a.getMax(), b.getMax()));
    }
}
