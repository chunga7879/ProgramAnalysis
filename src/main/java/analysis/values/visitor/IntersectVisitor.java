package analysis.values.visitor;

import analysis.values.EmptyValue;
import analysis.values.IntegerRange;
import analysis.values.IntegerValue;
import analysis.values.PossibleValues;

public class IntersectVisitor extends OperationVisitorWithDefault {
    @Override
    public PossibleValues visit(IntegerValue a, IntegerValue b) {
        if (a.getMin() > b.getMax()) return new EmptyValue();
        if (a.getMax() < b.getMin()) return new EmptyValue();
        return new IntegerRange(
                Math.max(a.getMin(), b.getMin()),
                Math.min(a.getMax(), b.getMax())
        );
    }

    // TODO: implement
}
