package analysis.model;

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

    public AnalysisError(String message) {
        this(message, false);
    }

    public String getMessage() {
        return message;
    }

    public boolean isDefinite() {
        return isDefinite;
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
