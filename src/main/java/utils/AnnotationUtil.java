package utils;

import analysis.model.AnalysisError;
import analysis.model.ExpressionAnalysisState;
import analysis.model.VariablesState;
import analysis.values.*;
import analysis.values.visitor.*;
import analysis.visitor.ExpressionVisitor;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;

import java.util.*;

/**
 * Util for annotations
 */
public final class AnnotationUtil {
    /**
     * All annotations we are analyzing
     */
    public enum AnnotationType {
        None,
        Null,
        NotNull,
        Min,
        Max,
        Negative,
        Positive,
        NegativeOrZero,
        PositiveOrZero,
        Size,
        NotEmpty,
    }

    /**
     * Check the return value against a list of annotations
     * @return Set of Errors from when the checks fail
     */
    public static List<AnalysisError> checkReturnValueWithAnnotation(
            PossibleValues value,
            List<AnnotationExpr> annotations,
            String nodeName
    ) {
        return checkWithAnnotations(value, annotations, "%s return is %s %s: " + nodeName);
    }

    public static List<AnalysisError> checkArgumentWithAnnotation(
            PossibleValues value,
            List<AnnotationExpr> annotations,
            String argName,
            String nodeName
    ) {
        return checkWithAnnotations(value, annotations, "Argument %s " + argName + " is %s %s: " + nodeName);
    }

    private static List<AnalysisError> checkWithAnnotations(
            PossibleValues value,
            List<AnnotationExpr> annotations,
            String format
    ) {
        if (value.isEmpty()) return Collections.emptyList();
        List<AnalysisError> errors = new ArrayList<>();
        Map<AnnotationType, Set<AnnotationExpr>> annotationMap = getAnnotationMap(annotations);
        ExpressionVisitor exprVisitor = null;
        if (annotationMap.containsKey(AnnotationType.NotNull) && value.canBeNull()) {
            errors.add(createError(format, "@NotNull", "null", value == NullValue.VALUE));
        }
        if (annotationMap.containsKey(AnnotationType.Positive)) {
            PairValue<Boolean, Boolean> check = checkPositiveAnnotation(value);
            if (!check.getA()) errors.add(createError(format, "@Positive", "zero or negative", check.getB()));
        }
        if (annotationMap.containsKey(AnnotationType.PositiveOrZero)) {
            PairValue<Boolean, Boolean> check; check = checkPositiveOrZeroAnnotation(value);
            if (!check.getA()) errors.add(createError(format, "@PositiveOrZero", "negative", check.getB()));
        }
        if (annotationMap.containsKey(AnnotationType.Negative)) {
            PairValue<Boolean, Boolean> check = checkNegativeAnnotation(value);
            if (!check.getA()) errors.add(createError(format, "@Negative", "zero or positive", check.getB()));
        }
        if (annotationMap.containsKey(AnnotationType.NegativeOrZero)) {
            PairValue<Boolean, Boolean> check = checkNegativeOrZeroAnnotation(value);
            if (!check.getA()) errors.add(createError(format, "@NegativeOrZero", "positive", check.getB()));
        }
        if (annotationMap.containsKey(AnnotationType.Min)) {
            exprVisitor = new ExpressionVisitor();
            PairValue<Boolean, Boolean> check = checkMinAnnotation(value, annotationMap.get(AnnotationType.Min), exprVisitor);
            if (!check.getA()) errors.add(createError(format, "@Min", "below min", check.getB()));
        }
        if (annotationMap.containsKey(AnnotationType.Max)) {
            if (exprVisitor == null) exprVisitor = new ExpressionVisitor();
            PairValue<Boolean, Boolean> check = checkMaxAnnotation(value, annotationMap.get(AnnotationType.Max), exprVisitor);
            if (!check.getA()) errors.add(createError(format, "@Max", "above max", check.getB()));
        }
        if (annotationMap.containsKey(AnnotationType.Size)) {
            if (exprVisitor == null) exprVisitor = new ExpressionVisitor();
            PairValue<Boolean, Boolean> check = checkSizeAnnotation(value, annotationMap.get(AnnotationType.Size), exprVisitor);
            if (!check.getA()) errors.add(createError(format, "@Size", "below or above size", check.getB()));
        }
        if (annotationMap.containsKey(AnnotationType.NotEmpty)) {
            PairValue<Boolean, Boolean> check = checkNotEmpty(value);
            if (!check.getA()) errors.add(createError(format, "@NotEmpty", "empty", check.getB()));
        }
        return errors;
    }

    private static PairValue<Boolean, Boolean> checkPositiveAnnotation(PossibleValues v) {
        if (v instanceof AnyValue) {
            // indefinite error
            return new PairValue<>(false, false);
        }

        if (v instanceof IntegerValue iv) {
            if (iv.getMax() <= 0) {
                // definite error
                return new PairValue<>(false, true);
            }
            if (iv.getMin() <= 0) {
                // indefinite error
                return new PairValue<>(false, false);
            }
        }
        return new PairValue<>(true, false);
    }

    private static PairValue<Boolean, Boolean> checkPositiveOrZeroAnnotation(PossibleValues v) {
        if (v instanceof AnyValue) {
            // indefinite error
            return new PairValue<>(false, false);
        }

        if (v instanceof IntegerValue iv) {
            if (iv.getMax() < 0) {
                // definite error
                return new PairValue<>(false, true);
            }
            if (iv.getMin() < 0) {
                // indefinite error
                return new PairValue<>(false, false);
            }
        }
        return new PairValue<>(true, false);
    }

    private static PairValue<Boolean, Boolean> checkNegativeAnnotation(PossibleValues v) {
        if (v instanceof AnyValue) {
            // indefinite error
            return new PairValue<>(false, false);
        }

        if (v instanceof IntegerValue iv) {
            if (iv.getMin() >= 0) {
                // definite error
                return new PairValue<>(false, true);
            }
            if (iv.getMax() >= 0) {
                // indefinite error
                return new PairValue<>(false, false);
            }
        }
        return new PairValue<>(true, false);
    }

    private static PairValue<Boolean, Boolean> checkNegativeOrZeroAnnotation(PossibleValues v) {
        if (v instanceof AnyValue) {
            // indefinite error
            return new PairValue<>(false, false);
        }

        if (v instanceof IntegerValue iv) {
            if (iv.getMin() > 0) {
                // definite error
                return new PairValue<>(false, true);
            }
            if (iv.getMax() > 0) {
                // indefinite error
                return new PairValue<>(false, false);
            }
        }
        return new PairValue<>(true, false);
    }

    private static PairValue<Boolean, Boolean> checkMinAnnotation(
            PossibleValues v,
            Set<AnnotationExpr> annotations,
            ExpressionVisitor exprVisitor
    ) {
        if (Objects.equals(v, AnyValue.VALUE)) {
            // indefinite error
            return new PairValue<>(false, false);
        }
        if (v instanceof IntegerValue) {
            PossibleValues lessThanMin = restrictValuesWithAnnotationParameters(
                    annotations,
                    exprVisitor,
                    v,
                    "value",
                    RestrictLessThanVisitor.INSTANCE
            );
            boolean pass = lessThanMin.isEmpty();
            return new PairValue<>(pass, !pass && Objects.equals(v, lessThanMin));
        }
        return new PairValue<>(true, false);
    }

    private static PairValue<Boolean, Boolean> checkMaxAnnotation(
            PossibleValues v,
            Set<AnnotationExpr> annotations,
            ExpressionVisitor exprVisitor
    ) {
        if (Objects.equals(v, AnyValue.VALUE)) {
            // indefinite error
            return new PairValue<>(false, false);
        }
        if (v instanceof IntegerValue) {
            PossibleValues moreThanMax = restrictValuesWithAnnotationParameters(
                    annotations,
                    exprVisitor,
                    v,
                    "value",
                    RestrictGreaterThanVisitor.INSTANCE
            );
            boolean pass = moreThanMax.isEmpty();
            return new PairValue<>(pass, !pass && Objects.equals(v, moreThanMax));
        }
        return new PairValue<>(true, false);
    }

    private static PairValue<Boolean, Boolean> checkSizeAnnotation(
            PossibleValues v,
            Set<AnnotationExpr> annotations,
            ExpressionVisitor exprVisitor
    ) {
        if (Objects.equals(v, AnyValue.VALUE)) {
            // indefinite error
            return new PairValue<>(false, false);
        }
        if (v instanceof ArrayValue arrayValue) {
            PossibleValues length = arrayValue.getLength();
            Map<String, List<Expression>> annotationParamMap = AnnotationUtil.getAnnotationParameterMap(annotations);
            List<Expression> minExpressions = annotationParamMap.get("min");
            List<Expression> maxExpressions = annotationParamMap.get("max");

            boolean pass = true;
            boolean isDefinite = false;
            if (minExpressions != null) {
                PossibleValues belowMinLength = restrictValueWithExpressions(length, RestrictLessThanVisitor.INSTANCE, minExpressions, exprVisitor);
                if (!belowMinLength.isEmpty()) {
                    pass = false;
                    if (Objects.equals(length, belowMinLength)) isDefinite = true;
                }
            }
            if (maxExpressions != null) {
                PossibleValues aboveMaxLength = restrictValueWithExpressions(length, RestrictGreaterThanVisitor.INSTANCE, maxExpressions, exprVisitor);
                if (!aboveMaxLength.isEmpty()) {
                    pass = false;
                    if (Objects.equals(length, aboveMaxLength)) isDefinite = true;
                }
            }
            return new PairValue<>(pass, isDefinite);
        }
        return new PairValue<>(true, false);
    }

    private static PairValue<Boolean, Boolean> checkNotEmpty(PossibleValues v) {
        if (v instanceof AnyValue) {
            // indefinite error
            return new PairValue<>(false, false);
        }

        if (Objects.equals(v, NullValue.VALUE)) {
            return  new PairValue<>(false, true);
        }

        if (v instanceof ArrayValue av) {
            boolean pass = !av.canBeNull() && av.getLength().getMin() > 0;
            boolean isDefinite = (av.getLength().getMin() == 0) && (av.getLength().getMin() == av.getLength().getMax());
            return new PairValue<>(pass, isDefinite);
        }

        if (v instanceof StringValue sv) {
            boolean pass = !sv.canBeNull() && sv.minStringLength() > 0;
            boolean isDefinite = (sv.minStringLength() == 0) && (sv.minStringLength() == sv.maxStringLength());
            return new PairValue<>(pass, isDefinite);
        }

        return new PairValue<>(true, false);
    }

    /**
     * Create annotation map from list of AnnotationExpr
     */
    public static Map<AnnotationType, Set<AnnotationExpr>> getAnnotationMap(List<AnnotationExpr> annotations) {
        Map<AnnotationType, Set<AnnotationExpr>> annotationMap = new HashMap<>();
        annotations.forEach(annotation -> {
            String annotationName = annotation.getNameAsString().toLowerCase();
            AnnotationType type = switch(annotationName) {
                case "null" -> AnnotationType.Null;
                case "notnull" -> AnnotationType.NotNull;
                case "min" -> AnnotationType.Min;
                case "max" -> AnnotationType.Max;
                case "negative" -> AnnotationType.Negative;
                case "negativeorzero" -> AnnotationType.NegativeOrZero;
                case "positive" -> AnnotationType.Positive;
                case "positiveorzero" -> AnnotationType.PositiveOrZero;
                case "size" -> AnnotationType.Size;
                case "notempty" -> AnnotationType.NotEmpty;
                default -> AnnotationType.None;
            };
            if (type != AnnotationType.None) {
                annotationMap.putIfAbsent(type, new HashSet<>());
                annotationMap.get(type).add(annotation);
            }
        });
        return annotationMap;
    }

    /**
     * Restrict value with annotation param value
     */
    public static PossibleValues restrictValuesWithAnnotationParameters(
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
    public static PossibleValues restrictValueWithExpressions(
            PossibleValues originalValue,
            RestrictionVisitor restrictionVisitor,
            List<Expression> valueExprs,
            ExpressionVisitor exprVisitor
    ) {
        if (originalValue.isEmpty()) return originalValue;
        for (Expression valueExpr : valueExprs) {
            PossibleValues val = valueExpr.accept(exprVisitor, new ExpressionAnalysisState(new VariablesState()));
            originalValue = originalValue.acceptAbstractOp(restrictionVisitor, val);
            if (originalValue.isEmpty()) return originalValue;
        }
        return originalValue;
    }

    /**
     * Get parameter value expression of an Annotation
     * (e.g. `@Size(min = 10)` & `min` -> 10)
     */
    public static List<Expression> getAnnotationParameterValue(Set<AnnotationExpr> annotations, String paramName) {
        return annotations.stream()
                .map(x -> {
                    if (!x.isNormalAnnotationExpr()) return null;
                    NodeList<MemberValuePair> pairs = x.asNormalAnnotationExpr().getPairs();
                    MemberValuePair valuePair = pairs.stream().filter(p -> p.getNameAsString().equalsIgnoreCase(paramName)).findAny().orElse(null);
                    return valuePair != null ? valuePair.getValue() : null;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Get parameter value expression map of an Annotation
     * (e.g. `@Size(min = 10, max = 12)` -> (`min` -> 10, `max` -> 12))
     */
    public static Map<String, List<Expression>> getAnnotationParameterMap(Set<AnnotationExpr> annotations) {
        Map<String, List<Expression>> annotationParamMap = new HashMap<>();
        annotations.forEach(x -> {
            if (!x.isNormalAnnotationExpr()) return;
            NodeList<MemberValuePair> pairs = x.asNormalAnnotationExpr().getPairs();
            pairs.forEach(p -> {
                annotationParamMap.putIfAbsent(p.getNameAsString(), new ArrayList<>());
                annotationParamMap.get(p.getNameAsString()).add(p.getValue());
            });
        });
        return annotationParamMap;
    }

    /**
     * Create an AnalysisError for the annotation error
     */
    private static AnalysisError createError(String format, String annotation, String badCondition, boolean isDefinite) {
        return new AnalysisError(String.format(format, annotation, (isDefinite ? "always" : "sometimes"), badCondition), isDefinite);
    }
}
