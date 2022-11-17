package analysis.values.visitor;

import analysis.values.*;

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

    @Override
    public PossibleValues visit(EmptyValue a, PossibleValues b) {
        return b;
    }

    @Override
    public PossibleValues visit(PossibleValues a, EmptyValue b) {
        return a;
    }
}
