package analysis.values;

import analysis.values.visitor.OperationVisitor;

public interface ValueVisitable {
    <T> T acceptAbstractOp(OperationVisitor<T> visitor, PossibleValues b);
    <T> T acceptOp(OperationVisitor<T> visitor, PossibleValues a);
    <T> T acceptOp(OperationVisitor<T> visitor, AnyValue a);
    <T> T acceptOp(OperationVisitor<T> visitor, EmptyValue a);
    <T> T acceptOp(OperationVisitor<T> visitor, IntegerValue a);
    <T> T acceptOp(OperationVisitor<T> visitor, StringValue a);
}