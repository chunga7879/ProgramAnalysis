package analysis.values.visitor;

import analysis.model.AnalysisError;
import analysis.values.*;

public abstract class AbstractOperationVisitor implements OperationVisitor<PairValue<PossibleValues, AnalysisError>> {
    @Override
    public PairValue<PossibleValues, AnalysisError> visitAbstract(ArrayValue a, PossibleValues b) {
        return b.acceptOp(this, a);
    }

    @Override
    public PairValue<PossibleValues, AnalysisError> visitAbstract(PossibleValues a, PossibleValues b) {
        return b.acceptOp(this, a);
    }

    @Override
    public PairValue<PossibleValues, AnalysisError> visitAbstract(AnyValue a, PossibleValues b) {
        return b.acceptOp(this, a);
    }

    @Override
    public PairValue<PossibleValues, AnalysisError> visitAbstract(EmptyValue a, PossibleValues b) {
        return b.acceptOp(this, a);
    }

    @Override
    public PairValue<PossibleValues, AnalysisError> visitAbstract(IntegerValue a, PossibleValues b) {
        return b.acceptOp(this, a);
    }

    @Override
    public PairValue<PossibleValues, AnalysisError> visitAbstract(StringValue a, PossibleValues b) {
        return b.acceptOp(this, a);
    }

    @Override
    public PairValue<PossibleValues, AnalysisError> visitAbstract(ObjectValue a, PossibleValues b) {
        return b.acceptOp(this, a);
    }

    @Override
    public PairValue<PossibleValues, AnalysisError> visitAbstract(NullValue a, PossibleValues b) {
        return b.acceptOp(this, a);
    }

    @Override
    public PairValue<PossibleValues, AnalysisError> visit(PossibleValues a, PossibleValues b) {
        return new PairValue<>(new AnyValue(), null);
    }

    @Override
    public PairValue<PossibleValues, AnalysisError> visit(EmptyValue a, PossibleValues b) {
        return new PairValue<>(a, null);
    }

    @Override
    public PairValue<PossibleValues, AnalysisError> visit(PossibleValues a, EmptyValue b) {
        return new PairValue<>(b, null);
    }

    @Override
    public PairValue<PossibleValues, AnalysisError> visit(IntegerValue a, IntegerValue b) {
        return new PairValue<>(new AnyValue(), null);
    }

    @Override
    public PairValue<PossibleValues, AnalysisError> visit(IntegerValue a, AnyValue b) {
        return new PairValue<>(new AnyValue(), null);
    }

    @Override
    public PairValue<PossibleValues, AnalysisError> visit(AnyValue a, IntegerValue b) {
        return new PairValue<>(new AnyValue(), null);
    }

    @Override
    public PairValue<PossibleValues, AnalysisError> visit(StringValue a, StringValue b) {
        return new PairValue<>(new AnyValue(), null);
    }

    @Override
    public PairValue<PossibleValues, AnalysisError> visit(PossibleValues a, StringValue b) {
        return new PairValue<>(new AnyValue(), null);
    }

    @Override
    public PairValue<PossibleValues, AnalysisError> visit(StringValue a, PossibleValues b) {
        return new PairValue<>(new AnyValue(), null);
    }

    @Override
    public PairValue<PossibleValues, AnalysisError> visit(NullValue a, ObjectValue b) {
        return new PairValue<>(new AnyValue(), null);
    }

    @Override
    public PairValue<PossibleValues, AnalysisError> visit(ObjectValue a, NullValue b) {
        return new PairValue<>(new AnyValue(), null);
    }

    @Override
    public PairValue<PossibleValues, AnalysisError> visit(ArrayValue a, ArrayValue b) {
        return new PairValue<>(new AnyValue(), null);
    }
}
