package analysis.exceptions;

/**
 * Exception when encountering an unsupposed node in the analysis
 */
public class UnsupportedNodeException extends RuntimeException {
    public UnsupportedNodeException(String message) {
        super(message);
    }
}
