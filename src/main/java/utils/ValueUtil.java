package utils;

import analysis.values.*;
import analysis.values.visitor.RestrictGreaterThanOrEqualVisitor;
import analysis.values.visitor.RestrictGreaterThanVisitor;
import analysis.values.visitor.RestrictLessThanOrEqualVisitor;
import analysis.values.visitor.RestrictLessThanVisitor;
import analysis.visitor.ExpressionVisitor;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.resolution.types.ResolvedPrimitiveType;
import com.github.javaparser.resolution.types.ResolvedType;
import utils.AnnotationUtil.AnnotationType;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static utils.AnnotationUtil.restrictValueWithExpressions;
import static utils.AnnotationUtil.restrictValuesWithAnnotationParameters;

public final class ValueUtil {

    /**
     * Get the default value for type
     */
    public static PossibleValues getValueForType(ResolvedType type) {
        if (type.isPrimitive()) {
            return switch (type.asPrimitive()) {
                case INT -> IntegerRange.ANY_VALUE;
                case CHAR -> CharValue.ANY_VALUE;
                case BOOLEAN -> BooleanValue.ANY_VALUE;
                default -> AnyValue.VALUE;
            };
        } else if (type.isArray()) {
            return ArrayValue.ANY_VALUE;
        } else if (type.isReferenceType()) {
            String qualifiedName = type.asReferenceType().getQualifiedName();
            return switch (qualifiedName) {
                case "java.lang.String" -> StringValue.ANY_VALUE;
                case "java.lang.Integer" -> BoxedPrimitive.create(IntegerRange.ANY_VALUE, true);
                case "java.lang.Boolean" -> BoxedPrimitive.create(BooleanValue.ANY_VALUE, true);
                case "java.lang.Character" -> BoxedPrimitive.create(CharValue.ANY_VALUE, true);
                default -> ExtendableObjectValue.VALUE;
            };
        }
        return AnyValue.VALUE;
    }

    /**
     * Get the initial value for type with annotations
     */
    public static PossibleValues getValueForType(ResolvedType type, List<AnnotationExpr> annotations, ExpressionVisitor exprVisitor) {
        Map<AnnotationType, Set<AnnotationExpr>> annotationMap = AnnotationUtil.getAnnotationMap(annotations);
        if (type.isPrimitive()) {
            return switch (type.asPrimitive()) {
                case INT -> getIntegerValue(annotationMap, exprVisitor);
                case CHAR -> CharValue.ANY_VALUE;
                case BOOLEAN -> BooleanValue.ANY_VALUE;
                default -> AnyValue.VALUE;
            };
        } else if (type.isArray()) {
            return getArrayValue(annotationMap, exprVisitor);
        } else if (type.isReferenceType()) {
            return getObjectValue(annotationMap, type, exprVisitor);
        }
        return AnyValue.VALUE;
    }

    /**
     * Get the initial value for an integer
     * using @Positive, @Negative, @PositiveOrZero, @NegativeOrZero, @Min, and @Max
     */
    private static PossibleValues getIntegerValue(Map<AnnotationType, Set<AnnotationExpr>> annotationMap, ExpressionVisitor exprVisitor) {
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
        return integerValue;
    }

    /**
     * Get the initial value for the array
     * using @NotNull, @Null, @NotEmpty, and @Size annotations
     */
    private static PossibleValues getArrayValue(Map<AnnotationType, Set<AnnotationExpr>> annotationMap, ExpressionVisitor exprVisitor) {
        // @NotEmpty annotation
        boolean isNotEmpty = annotationMap.containsKey(AnnotationType.NotEmpty);

        // @NotNull & @Null annotation
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

    /**
     * Get the initial value for an object using @NotNull and @Null
     * Additionally:
     * - String: @NotEmpty
     * - Integer: @Positive, @Negative, @PositiveOrZero, @NegativeOrZero, @Min, and @Max
     * - Boolean, Character: none
     */
    private static PossibleValues getObjectValue(Map<AnnotationType, Set<AnnotationExpr>> annotationMap, ResolvedType type, ExpressionVisitor exprVisitor) {
        boolean isNotNull = annotationMap.containsKey(AnnotationType.NotNull);
        boolean isNull = annotationMap.containsKey(AnnotationType.Null);
        boolean isNotEmpty = annotationMap.containsKey(AnnotationType.NotEmpty);
        isNotNull = isNotNull || isNotEmpty;
        if (isNull && isNotNull) return EmptyValue.VALUE;
        if (isNull) return NullValue.VALUE;

        String name = type.asReferenceType().getQualifiedName();
        return switch (name) {
            case "java.lang.String" -> new StringValue(isNotEmpty ? 1 : 0, Integer.MAX_VALUE, !isNotNull);
            case "java.lang.Integer" -> BoxedPrimitive.create(getIntegerValue(annotationMap, exprVisitor), !isNotNull);
            case "java.lang.Boolean" -> BoxedPrimitive.create(getValueForType(ResolvedPrimitiveType.BOOLEAN), !isNotNull);
            case "java.lang.Character" -> BoxedPrimitive.create(getValueForType(ResolvedPrimitiveType.CHAR), !isNotNull);
            default -> new ExtendableObjectValue(!isNotNull);
        };
    }

    public static PossibleValues stringValueConcat(PossibleValues left, PossibleValues right) {
        if (!left.isEmpty() && !right.isEmpty()) {
            return new StringValue(
                    MathUtil.addToLimit(left.minStringLength(), right.minStringLength()),
                    MathUtil.addToLimit(left.maxStringLength(), right.maxStringLength())
            );
        }
        return EmptyValue.VALUE;
    }
}
