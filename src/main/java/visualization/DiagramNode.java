package visualization;

import java.util.List;

/**
 * Represents a node in the flowchart diagram
 * @param statement Statement that the node represents
 * @param errorType POTENTIAL, DEFINITE, ERROR; if a node has multiple errors, as long as it has one definite error, it is considered definite
 * @param errors List of all errors associated with the node
 */
public record DiagramNode(String statement, ErrorType errorType, List<Error> errors) {
}
