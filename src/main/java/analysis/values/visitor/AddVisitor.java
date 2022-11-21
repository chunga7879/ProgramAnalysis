package analysis.values.visitor;

import analysis.values.*;
import utils.MathUtil;

/**
 * Visitor getting possible values for addition operation: a + b
 */
public class AddVisitor extends OperationVisitorWithDefault {
    @Override
    public PossibleValues visit(IntegerValue a, IntegerValue b) {
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

    @Override
    public PossibleValues visit(NullValue a, ObjectValues b) {
        // TODO: implement
        return new StringValue();
    }

    @Override
    public PossibleValues visit(ObjectValues a, NullValue b) {
        // TODO: implement
        return new StringValue();
    }
}
