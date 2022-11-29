package analysis.values.visitor;

import analysis.values.*;

/**
 * Visitor for value operation
 *
 * @param <T>
 */
public interface OperationVisitor<T> {
    T visitAbstract(PossibleValues a, PossibleValues b);

    T visitAbstract(AnyValue a, PossibleValues b);

    T visitAbstract(EmptyValue a, PossibleValues b);

    T visitAbstract(IntegerValue a, PossibleValues b);

    T visitAbstract(StringValue a, PossibleValues b);

    T visitAbstract(ObjectValue a, PossibleValues b);

    T visitAbstract(NullValue a, PossibleValues b);

    T visitAbstract(ArrayValue a, PossibleValues b);

    T visitAbstract(CharValue a, PossibleValues b);

    T visitAbstract(BooleanValue a, PossibleValues b);

    T visitAbstract(BoxedPrimitive a, PossibleValues b);

    T visit(PossibleValues a, PossibleValues b);

    T visit(EmptyValue a, PossibleValues b);

    T visit(PossibleValues a, EmptyValue b);

    T visit(IntegerValue a, IntegerValue b);

    T visit(IntegerValue a, AnyValue b);

    T visit(AnyValue a, IntegerValue b);

    T visit(StringValue a, StringValue b);

    T visit(PossibleValues a, StringValue b);

    T visit(StringValue a, PossibleValues b);

    T visit(NullValue a, ObjectValue b);

    T visit(ObjectValue a, NullValue b);

    T visit(CharValue a, CharValue b);

    T visit(CharValue a, IntegerValue b);

    T visit(IntegerValue a, CharValue b);

    T visit(BooleanValue a, BooleanValue b);

    T visit(ArrayValue a, ArrayValue b);

    T visit(BoxedPrimitive a, BoxedPrimitive b);

    T visit(BoxedPrimitive a, PrimitiveValue b);

    T visit(PrimitiveValue a, BoxedPrimitive b);

    T visit (NullValue a, PossibleValues b);

    T visit (PossibleValues a, NullValue b);
}
