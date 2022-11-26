package utils;

import analysis.model.AnalysisError;
import analysis.values.NullValue;
import analysis.values.ObjectValue;
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
        Size,
    }

    /**
     * Check the return value against a list of annotations
     * @return Set of Errors from when the checks fail
     */
    public static Set<AnalysisError> checkReturnValueWithAnnotation(PossibleValues values, List<AnnotationExpr> annotations) {
        Set<AnalysisError> errors = new HashSet<>();
        Map<AnnotationType, Set<AnnotationExpr>> annotationMap = new HashMap<>();
        annotations.forEach(annotation -> {
            String annotationName = annotation.getNameAsString().toLowerCase();
            AnnotationType type = switch(annotationName) {
                case "null" -> AnnotationType.Null;
                case "notnull" -> AnnotationType.NotNull;
                case "min" -> AnnotationType.Min;
                case "max" -> AnnotationType.Max;
                case "size" -> AnnotationType.Size;
                default -> AnnotationType.None;
            };
            if (type != AnnotationType.None) {
                annotationMap.putIfAbsent(type, new HashSet<>());
                annotationMap.get(type).add(annotation);
            }
        });
        if (values instanceof ObjectValue objValue) {
            if (annotationMap.containsKey(AnnotationType.NotNull) && objValue.canBeNull()) {
                boolean isDefinite = objValue == NullValue.VALUE;
                errors.add(createReturnError("@NotNull", "null", isDefinite));
            }
            if (annotationMap.containsKey(AnnotationType.Null) && objValue != NullValue.VALUE) {
                boolean isDefinite = !objValue.canBeNull();
                errors.add(createReturnError("@Null", "not null", isDefinite));
            }
        }
        // TODO: Integer, Array
        return errors;
    }

    /**
     * Create a AnalysisError for the return annotation error
     */
    private static AnalysisError createReturnError(String annotation, String badCondition, boolean isDefinite) {
        String message = annotation + " return is " + (isDefinite ? "always " : "sometimes ") + badCondition;
        return new AnalysisError(message, isDefinite);
    }
}
