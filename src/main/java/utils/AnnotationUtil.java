package utils;

import analysis.model.AnalysisError;
import analysis.values.*;
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

    public static Set<AnalysisError> checkArgumentWithAnnotation(
            PossibleValues value,
            List<AnnotationExpr> annotations
    ) {
        Set<AnalysisError> errors = new HashSet<>();
        Map<AnnotationType, Set<AnnotationExpr>> annotationMap = getAnnotationMap(annotations);
        if (annotationMap.containsKey(AnnotationType.Null) && value.canBeNull()) {
            boolean isDefinite = value == NullValue.VALUE;
        }
        if (annotationMap.containsKey(AnnotationType.Positive) && !checkPositiveAnnotation(value)) {
            errors.add(createArgumentError("@Positive", "not positive", false));
        }
        if (annotationMap.containsKey(AnnotationType.PositiveOrZero) && !checkPositiveOrZeroAnnotation(value)) {
            errors.add(createArgumentError("@PositiveOrZero", "not positive or zero", false));
        }
        if (annotationMap.containsKey(AnnotationType.Negative) && !checkNegativeAnnotation(value)) {
            errors.add(createArgumentError("@Negative", "not negative", false));
        }
        if (annotationMap.containsKey(AnnotationType.NegativeOrZero) && !checkNegativeOrZeroAnnotation(value)) {
            errors.add(createArgumentError("@NegativeOrZero", "not negative or zero", false));
        }
        return errors;
    }

    private static boolean checkPositiveAnnotation(PossibleValues v) {
        if (v instanceof AnyValue) {
            // indefinite error
            return false;
        }

        if (v instanceof IntegerValue iv) {
            if (iv.getMax() <= 0) {
                // definite error
                return false;
            }
            if (iv.getMin() <= 0) {
                // indefinite error
                return false;
            }
        }
        return true;
    }

    private static boolean checkPositiveOrZeroAnnotation(PossibleValues v) {
        // TODO: implement
        return true;
    }

    private static boolean checkNegativeAnnotation(PossibleValues v) {
        // TODO: implement
        return true;
    }

    private static boolean checkNegativeOrZeroAnnotation(PossibleValues v) {
        // TODO: implement
        return true;
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

    private static AnalysisError createArgumentError(String annotation, String badCondition, boolean isDefinite) {
        String message = annotation + "";
        return new AnalysisError(message, isDefinite);
    }
}
