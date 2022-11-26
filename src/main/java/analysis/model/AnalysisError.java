package analysis.model;

import java.util.Objects;

/**
 * Error found by the analysis
 */
public class AnalysisError {
    private String message;
    // TODO: differentiate between potential errors, absolute errors, warnings, etc...

    public AnalysisError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
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
