package analysis.model;

import com.github.javaparser.ast.Node;

import java.util.*;

/**
 * State of the analysis
 */
public class AnalysisState {
    private VariablesState variablesState;

    private Map<Node, Set<AnalysisError>> errorMap;

    public AnalysisState(VariablesState variablesState) {
        this.variablesState = variablesState;
        this.errorMap = new HashMap<>();
    }

    public VariablesState getVariablesState() {
        return variablesState;
    }

    public Map<Node, Set<AnalysisError>> getErrorMap() {
        return errorMap;
    }

    public void addErrors(Node node, Set<AnalysisError> errors) {
        if (errors == null || errors.isEmpty()) return;
        if (errorMap.containsKey(node)) {
            errorMap.get(node).addAll(errors);
        } else {
            errorMap.put(node, new HashSet<>(errors));
        }
    }

    public void addErrors(Node node, List<AnalysisError> errors) {
        if (errors == null || errors.isEmpty()) return;
        addErrors(node, new HashSet<>(errors));
    }

    public void addErrors(AnalysisState state) {
        for (Map.Entry<Node, Set<AnalysisError>> entry : state.errorMap.entrySet()) {
            addErrors(entry.getKey(), entry.getValue());
        }
    }
}
