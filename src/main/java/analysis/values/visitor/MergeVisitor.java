package analysis.values.visitor;

import analysis.values.AnyValue;
import analysis.values.IntegerRange;
import analysis.values.PossibleValues;
import analysis.values.StringValue;

/**
 * Visitor for getting possible values after merging operation of a & b
 */
public class MergeVisitor extends OperationVisitorWithDefault {
    @Override
    public PossibleValues visit(IntegerRange a, IntegerRange b) {
        return new IntegerRange(
                Math.min(a.getMin(), b.getMin()),
                Math.max(a.getMax(), b.getMax())
        );
    }

    @Override
    public PossibleValues visit(StringValue a, StringValue b) {
        // TODO: implement merge
        return new StringValue();
    }
}
