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
    T visitAbstract(IntegerValue a, PossibleValues b);
    T visitAbstract(StringValue a, PossibleValues b);

    T visit(PossibleValues a, PossibleValues b);
    T visit(EmptyValue a, PossibleValues b);
    T visit(PossibleValues a, EmptyValue b);
    T visit(IntegerValue a, IntegerValue b);
    T visit(IntegerValue a, AnyValue b);
    T visit(AnyValue a, IntegerValue b);
    T visit(StringValue a, StringValue b);
    T visit(PossibleValues a, StringValue b);
    T visit(StringValue a, PossibleValues b);
}
