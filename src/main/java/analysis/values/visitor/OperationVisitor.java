package analysis.values.visitor;

import analysis.values.*;

/**
 * Visitor for value operation
 * @param <T>
 */
public interface OperationVisitor<T> {
    T visitAbstract(PossibleValues a, PossibleValues b);
    T visitAbstract(AnyValue a, PossibleValues b);
    T visitAbstract(EmptyValue a, PossibleValues b);
    T visitAbstract(IntegerRange a, PossibleValues b);
    T visitAbstract(StringValue a, PossibleValues b);

    T visit(PossibleValues a, PossibleValues b);
    T visit(EmptyValue a, PossibleValues b);
    T visit(PossibleValues a, EmptyValue b);
    T visit(IntegerRange a, IntegerRange b);
    T visit(IntegerRange a, AnyValue b);
    T visit(AnyValue a, IntegerRange b);
    T visit(StringValue a, StringValue b);
    T visit(PossibleValues a, StringValue b);
    T visit(StringValue a, PossibleValues b);
}
