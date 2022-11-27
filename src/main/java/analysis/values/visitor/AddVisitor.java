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
        return this.visit(a, (PossibleValues) b);
    }

    @Override
    public PossibleValues visit(PossibleValues a, StringValue b) {
        return new StringValue(
                MathUtil.addToLimit(a.minStringLength(), b.minStringLength()),
                MathUtil.addToLimit(a.maxStringLength(), b.maxStringLength()),
                false
        );
    }

    @Override
    public PossibleValues visit(StringValue a, PossibleValues b) {
        return this.visit(b, a);
    }

    @Override
    public PossibleValues visit(NullValue a, ObjectValue b) {
        return new StringValue(
                MathUtil.addToLimit(a.minStringLength(), b.minStringLength()),
                MathUtil.addToLimit(a.maxStringLength(), b.maxStringLength()),
                false
        );
    }

    @Override
    public PossibleValues visit(ObjectValue a, NullValue b) {
        return this.visit(b, a);
    }

    @Override
    public PossibleValues visit(CharValue a, CharValue b) {
        return new IntegerRange(
                MathUtil.addToLimit(a.getMin(), b.getMin()),
                MathUtil.addToLimit(a.getMax(), b.getMax())
        );
    }

    @Override
    public PossibleValues visit(CharValue a, IntegerValue b) {
        return new IntegerRange(
                MathUtil.addToLimit(a.getMin(), b.getMin()),
                MathUtil.addToLimit(a.getMax(), b.getMax())
        );
    }

    @Override
    public PossibleValues visit(IntegerValue a, CharValue b) {
        return this.visit(b, a);
    }
}
