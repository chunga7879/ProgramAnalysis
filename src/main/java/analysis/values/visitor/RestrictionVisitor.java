package analysis.values.visitor;

import analysis.values.AnyValue;
import analysis.values.IntegerValue;
import analysis.values.PossibleValues;

public abstract class RestrictionVisitor extends OperationVisitorWithDefault {
    @Override
    public PossibleValues visit(IntegerValue a, AnyValue b) {
        return a;
    }

    @Override
    public PossibleValues visit(AnyValue a, IntegerValue b) {
        return b;
    }
}
