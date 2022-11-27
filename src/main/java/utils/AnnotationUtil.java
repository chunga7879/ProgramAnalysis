package utils;

import analysis.model.AnalysisError;
import analysis.values.NullValue;
import analysis.values.PossibleValues;
import com.github.javaparser.ast.expr.AnnotationExpr;

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
    private static Map<AnnotationType, Set<AnnotationExpr>> getAnnotationMap(List<AnnotationExpr> annotations) {
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
     * Create a AnalysisError for the return annotation error
     */
    private static AnalysisError createReturnError(String annotation, String badCondition, String nodeName, boolean isDefinite) {
        String message = annotation + " return is " + (isDefinite ? "always " : "sometimes ") + badCondition + ": " + nodeName;
        return new AnalysisError(message, isDefinite);
    }
}
