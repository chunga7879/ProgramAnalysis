package utils;

import analysis.model.AnalysisError;
import analysis.values.NullValue;
import analysis.values.PossibleValues;
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
    public static Set<AnalysisError> checkReturnValueWithAnnotation(
            PossibleValues value,
            List<AnnotationExpr> annotations,
            String nodeName
    ) {
        Set<AnalysisError> errors = new HashSet<>();
        Map<AnnotationType, Set<AnnotationExpr>> annotationMap = getAnnotationMap(annotations);
        if (annotationMap.containsKey(AnnotationType.NotNull) && value.canBeNull()) {
            boolean isDefinite = value == NullValue.VALUE;
            errors.add(createReturnError("@NotNull", "null", nodeName, isDefinite));
        }
        if (annotationMap.containsKey(AnnotationType.Null) && value != NullValue.VALUE) {
            boolean isDefinite = !value.canBeNull();
            errors.add(createReturnError("@Null", "not null", nodeName, isDefinite));
        }
        // TODO: Integer, Array
        return errors;
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
     * Create a AnalysisError for the return annotation error
     */
    private static AnalysisError createReturnError(String annotation, String badCondition, String nodeName, boolean isDefinite) {
        String message = annotation + " return is " + (isDefinite ? "always " : "sometimes ") + badCondition + ": " + nodeName;
        return new AnalysisError(message, isDefinite);
    }
}
