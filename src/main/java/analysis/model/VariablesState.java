package analysis.model;

import com.github.javaparser.ast.Node;

import java.util.Map;

public class VariablesState {
    private Map<Node, PossibleValue> variableMap;
}
