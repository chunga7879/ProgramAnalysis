package utils;

import analysis.model.ExpressionAnalysisState;
import analysis.model.VariablesState;
import analysis.values.*;
import analysis.visitor.ExpressionVisitor;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.resolution.types.ResolvedType;

import java.util.List;

public final class ValueUtil {

    /**
     * Get the default value for type
     */
    public static PossibleValues getValueForType(ResolvedType type) {
        if (type.isPrimitive()) {
            return switch (type.asPrimitive()) {
                case INT -> IntegerRange.ANY_VALUE;
                case BOOLEAN -> AnyValue.VALUE;
                default -> AnyValue.VALUE;
            };
        } else if (type.isArray()) {
            return ArrayValue.ANY_VALUE;
        } else if (type.isReferenceType()) {
            String qualifiedName = type.asReferenceType().getQualifiedName();
            if (qualifiedName.equalsIgnoreCase("java.lang.String")) {
                return StringValue.ANY_VALUE;
            }
        }
        return AnyValue.VALUE;
    }

    /**
     * Get the initial value for type with annotations
     */
    public static PossibleValues getValueForType(ResolvedType type, List<AnnotationExpr> annotations, VariablesState state, ExpressionVisitor exprVisitor) {
        // TODO: handle more annotations, types
        if (type.isPrimitive()) {
            return switch (type.asPrimitive()) {
                case INT -> IntegerRange.ANY_VALUE;
                case BOOLEAN -> AnyValue.VALUE;
                default -> AnyValue.VALUE;
            };
        } else if (type.isArray()) {
            return getArrayValue(annotations, state, exprVisitor);
        } else if (type.isReferenceType()) {
        }
        return AnyValue.VALUE;
    }

    /**
     * Get the initial value for the array
     * using @NotNull and @Size annotations
     */
    private static ArrayValue getArrayValue(List<AnnotationExpr> annotations, VariablesState state, ExpressionVisitor exprVisitor) {
        if (annotations == null) return ArrayValue.ANY_VALUE;

        // @Size annotation
        AnnotationExpr sizeAnnotation = annotations.stream()
                .filter(x -> x.getNameAsString().equalsIgnoreCase("size") && x.isNormalAnnotationExpr())
                .findAny()
                .orElse(null);
        IntegerValue length;
        if (sizeAnnotation != null) {
            NodeList<MemberValuePair> memValPair = sizeAnnotation.asNormalAnnotationExpr().getPairs();
            MemberValuePair minMemValPair = memValPair.stream().filter(x -> x.getNameAsString().equalsIgnoreCase("min")).findAny().orElse(null);
            MemberValuePair maxMemValPair = memValPair.stream().filter(x -> x.getNameAsString().equalsIgnoreCase("max")).findAny().orElse(null);
            int min = ArrayValue.MIN_LENGTH_NUM;
            if (minMemValPair != null) {
                PossibleValues minValue = minMemValPair.getValue().accept(exprVisitor, new ExpressionAnalysisState(state.copy()));
                if (minValue instanceof IntegerValue minIntValue && minIntValue.getMin() >= ArrayValue.MIN_LENGTH_NUM) {
                    min = minIntValue.getMin();
                }
            }
            int max = ArrayValue.MAX_LENGTH_NUM;
            if (maxMemValPair != null) {
                PossibleValues maxValue = maxMemValPair.getValue().accept(exprVisitor, new ExpressionAnalysisState(state.copy()));
                if (maxValue instanceof IntegerValue maxIntValue && maxIntValue.getMax() >= min) {
                    maxIntValue.getMax();
                    max = maxIntValue.getMax();
                }
            }
            length = new IntegerRange(min, max);
        } else {
            length = ArrayValue.DEFAULT_LENGTH;
        }

        // @NotNull annotation
        boolean notNull = annotations.stream().anyMatch(x -> x.getNameAsString().equalsIgnoreCase("notnull"));

        return new ArrayValue(length, !notNull);
    }
}
