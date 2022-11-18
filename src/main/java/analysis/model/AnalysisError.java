package analysis.model;

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
}
