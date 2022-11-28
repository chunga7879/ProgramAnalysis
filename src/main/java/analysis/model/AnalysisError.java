package analysis.model;

import com.github.javaparser.ast.Node;

import java.util.Objects;

/**
 * Error found by the analysis
 */
public class AnalysisError {
    private final String message;
    private final boolean isDefinite;

    public AnalysisError(String message, boolean isDefinite) {
        this.message = message;
        this.isDefinite = isDefinite;
    }

    @Deprecated
    public AnalysisError(String message) {
        this(message, false);
    }

    public AnalysisError(Class<? extends RuntimeException> exception, boolean isDefinite) {
        this(exception.getSimpleName(), isDefinite);
    }

    public AnalysisError(Class<? extends RuntimeException> exception, Node n, boolean isDefinite) {
        this(exception.getSimpleName() + ": " + n.toString(), isDefinite);
    }

    public AnalysisError(String exception, Node n, boolean isDefinite) {
        this(exception + ": " + n.toString(), isDefinite);
    }

    public String getMessage() {
        return message;
    }

    public boolean isDefinite() {
        return isDefinite;
    }

    public AnalysisError atNode(Node node) {
        return new AnalysisError(this.message + ": " + node, this.isDefinite);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnalysisError that)) return false;

        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return message != null ? message.hashCode() : 0;
    }
}
