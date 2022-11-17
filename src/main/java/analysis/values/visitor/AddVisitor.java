package analysis.values.visitor;

import analysis.values.IntegerRange;
import analysis.values.PossibleValues;
import analysis.values.StringValue;
import utils.MathUtil;

/**
 * Visitor getting possible values for addition operation: a + b
 */
public class AddVisitor extends OperationVisitorWithDefault {
    @Override
    public PossibleValues visit(IntegerRange a, IntegerRange b) {
        return new IntegerRange(
                MathUtil.addToLimit(a.getMin(), b.getMin()),
                MathUtil.addToLimit(a.getMax(), b.getMax())
        );
    }

    @Override
    public PossibleValues visit(StringValue a, StringValue b) {
        // TODO: implement
        return new StringValue();
    }

    @Override
    public PossibleValues visit(PossibleValues a, StringValue b) {
        // TODO: implement
        return new StringValue();
    }

    @Override
    public PossibleValues visit(StringValue a, PossibleValues b) {
        // TODO: implement
        return new StringValue();
    }
}
