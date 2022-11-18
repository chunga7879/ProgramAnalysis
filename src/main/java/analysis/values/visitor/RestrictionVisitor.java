package analysis.values.visitor;

import analysis.values.AnyValue;
import analysis.values.IntegerRange;
import analysis.values.PossibleValues;

public abstract class RestrictionVisitor extends OperationVisitorWithDefault {
    @Override
    public PossibleValues visit(IntegerRange a, AnyValue b) {
        return a;
    }

    @Override
    public PossibleValues visit(AnyValue a, IntegerRange b) {
        return b;
    }
}
