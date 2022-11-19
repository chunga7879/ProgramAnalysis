package analysis.values.visitor;

import analysis.values.AnyValue;
import analysis.values.IntegerRange;
import analysis.values.PossibleValues;
import analysis.values.StringValue;

public abstract class OperationVisitorWithDefault implements OperationVisitor<PossibleValues> {
    @Override
    public PossibleValues visitAbstract(PossibleValues a, PossibleValues b) {
        return b.acceptOp(this, a);
    }

    @Override
    public PossibleValues visitAbstract(AnyValue a, PossibleValues b) {
        return b.acceptOp(this, a);
    }

    @Override
    public PossibleValues visitAbstract(IntegerRange a, PossibleValues b) {
        return b.acceptOp(this, a);
    }

    @Override
    public PossibleValues visitAbstract(StringValue a, PossibleValues b) {
        return b.acceptOp(this, a);
    }

    @Override
    public PossibleValues visit(PossibleValues a, PossibleValues b) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(IntegerRange a, IntegerRange b) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(StringValue a, StringValue b) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(PossibleValues a, StringValue b) {
        return new AnyValue();
    }

    @Override
    public PossibleValues visit(StringValue a, PossibleValues b) {
        return new AnyValue();
    }
}
