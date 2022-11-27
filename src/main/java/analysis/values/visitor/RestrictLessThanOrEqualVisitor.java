package analysis.values.visitor;

import analysis.values.EmptyValue;
import analysis.values.IntegerRange;
import analysis.values.IntegerValue;
import analysis.values.PossibleValues;

public class RestrictLessThanOrEqualVisitor extends RestrictionVisitor {
    public static final RestrictLessThanOrEqualVisitor INSTANCE = new RestrictLessThanOrEqualVisitor();

    @Override
    public PossibleValues visit(IntegerValue a, IntegerValue b) {
        if (a.getMin() > b.getMax()) return new EmptyValue();
        return new IntegerRange(a.getMin(), Math.min(a.getMax(), b.getMax()));
    }
}
