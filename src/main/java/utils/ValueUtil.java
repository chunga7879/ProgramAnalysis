package utils;

import analysis.model.ExpressionAnalysisState;
import analysis.model.VariablesState;
import analysis.values.*;
import analysis.values.visitor.*;
import analysis.visitor.ExpressionVisitor;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.resolution.types.ResolvedType;
import utils.AnnotationUtil.AnnotationType;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
        Map<AnnotationType, Set<AnnotationExpr>> annotationMap = AnnotationUtil.getAnnotationMap(annotations);
        if (type.isPrimitive()) {
            return switch (type.asPrimitive()) {
                case INT -> {
                    PossibleValues integerValue = IntegerRange.ANY_VALUE;
                    if (annotationMap.containsKey(AnnotationType.Min)) {
                        integerValue = restrictValuesWithAnnotationParameters(
                                annotationMap.get(AnnotationType.Min),
                                exprVisitor,
                                integerValue,
                                "value",
                                RestrictGreaterThanOrEqualVisitor.INSTANCE
                        );
                    }
                    if (annotationMap.containsKey(AnnotationType.Max)) {
                        integerValue = restrictValuesWithAnnotationParameters(
                                annotationMap.get(AnnotationType.Max),
                                exprVisitor,
                                integerValue,
                                "value",
                                RestrictLessThanOrEqualVisitor.INSTANCE
                        );
                    }
                    if (annotationMap.containsKey(AnnotationType.Negative)) {
                        integerValue = integerValue.acceptAbstractOp(RestrictLessThanVisitor.INSTANCE, new IntegerRange(0));
                    }
                    if (annotationMap.containsKey(AnnotationType.Positive)) {
                        integerValue = integerValue.acceptAbstractOp(RestrictGreaterThanVisitor.INSTANCE, new IntegerRange(0));
                    }
                    if (annotationMap.containsKey(AnnotationType.NegativeOrZero)) {
                        integerValue = integerValue.acceptAbstractOp(RestrictLessThanOrEqualVisitor.INSTANCE, new IntegerRange(0));
                    }
                    if (annotationMap.containsKey(AnnotationType.PositiveOrZero)) {
                        integerValue = integerValue.acceptAbstractOp(RestrictGreaterThanOrEqualVisitor.INSTANCE, new IntegerRange(0));
                    }
                    yield integerValue;
                }
                case BOOLEAN -> new BooleanValue(true, true);
                default -> AnyValue.VALUE;
            };
        } else if (type.isArray()) {
            return getArrayValue(annotationMap, state, exprVisitor);
        } else if (type.isReferenceType()) {
            boolean isNotNull = annotationMap.containsKey(AnnotationType.NotNull);
            boolean isNull = annotationMap.containsKey(AnnotationType.Null);
            boolean isNotEmpty = annotationMap.containsKey(AnnotationType.NotEmpty);
            isNotNull = isNotNull || isNotEmpty;
            if (isNull && isNotNull) return EmptyValue.VALUE;
            if (isNull) return NullValue.VALUE;

            String name = type.asReferenceType().getQualifiedName();
            if (name.equalsIgnoreCase("java.lang.String")) {
                StringValue baseValue = isNotEmpty ? new StringValue(1, Integer.MAX_VALUE) : StringValue.ANY_VALUE;
                return isNotNull ? baseValue.withNotNullable() : baseValue;
            }
        }
        return AnyValue.VALUE;
    }

    /**
     * Restrict value with annotation param value
     */
    private static PossibleValues restrictValuesWithAnnotationParameters(
            Set<AnnotationExpr> annotations,
            ExpressionVisitor exprVisitor,
            PossibleValues originalValue,
            String paramName,
            RestrictionVisitor restrictionVisitor
    ) {
        if (originalValue.isEmpty()) return originalValue;
        List<Expression> valueExprs = AnnotationUtil.getAnnotationParameterValue(annotations, paramName);
        return restrictValueWithExpressions(originalValue, restrictionVisitor, valueExprs, exprVisitor);
    }

    /**
     * Restrict value with expressions
     */
    private static PossibleValues restrictValueWithExpressions(
            PossibleValues originalValue,
            RestrictionVisitor restrictionVisitor,
            List<Expression> valueExprs,
            ExpressionVisitor exprVisitor
    ) {
        if (originalValue.isEmpty()) return originalValue;
        for (Expression valueExpr : valueExprs) {
            PossibleValues val = valueExpr.accept(exprVisitor, new ExpressionAnalysisState(new VariablesState()));
            originalValue = originalValue.acceptAbstractOp(restrictionVisitor, val);
        }
        return originalValue;
    }

    /**
     * Get the initial value for the array
     * using @NotNull and @Size annotations
     */
    private static PossibleValues getArrayValue(Map<AnnotationType, Set<AnnotationExpr>> annotationMap, VariablesState state, ExpressionVisitor exprVisitor) {
        // @NotEmpty annotation
        boolean isNotEmpty = annotationMap.containsKey(AnnotationType.NotEmpty);

        // @NotNull annotation
        boolean isNotNull = annotationMap.containsKey(AnnotationType.NotNull);
        boolean isNull = annotationMap.containsKey(AnnotationType.Null);
        isNotNull = isNotNull || isNotEmpty;

        if (isNull && isNotNull) return EmptyValue.VALUE;
        if (isNull) return NullValue.VALUE;


        // @Size annotation
        Set<AnnotationExpr> sizeAnnotations = annotationMap.get(AnnotationType.Size);

        PossibleValues length = isNotEmpty ? new IntegerRange(1, ArrayValue.MAX_LENGTH_NUM) : ArrayValue.DEFAULT_LENGTH;
        if (sizeAnnotations != null) {
            Map<String, List<Expression>> annotationParamMap = AnnotationUtil.getAnnotationParameterMap(sizeAnnotations);
            List<Expression> minExpressions = annotationParamMap.get("min");
            List<Expression> maxExpressions = annotationParamMap.get("max");

            if (minExpressions != null) {
                length = restrictValueWithExpressions(length, RestrictGreaterThanOrEqualVisitor.INSTANCE, minExpressions, exprVisitor);
            }
            if (maxExpressions != null) {
                length = restrictValueWithExpressions(length, RestrictLessThanOrEqualVisitor.INSTANCE, maxExpressions, exprVisitor);
            }
        }

        return ArrayValue.create(length, !isNotNull);
    }
}
