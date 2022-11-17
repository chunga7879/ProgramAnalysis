package analysis.model;

import analysis.values.PossibleValues;
import com.github.javaparser.ast.Node;

import java.util.HashMap;
import java.util.Map;

public class VariablesState {
    private final Map<Node, PossibleValues> variableMap;

    public VariablesState() {
        this.variableMap = new HashMap<>();
    }

    public void setVariable(Node node, PossibleValues value) {
        variableMap.put(node, value);
    }

    public PossibleValues getVariable(Node node) {
        return variableMap.get(node);
    }

    /**
     * Merge a state with another state
     */
    public VariablesState merge(VariablesState other) {
        // TODO: implement
        return null;
    }
}
