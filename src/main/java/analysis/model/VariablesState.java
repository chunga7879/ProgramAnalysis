package analysis.model;

import analysis.values.PossibleValues;
import com.github.javaparser.ast.Node;

import java.util.Map;

public class VariablesState {
    private Map<Node, PossibleValues> variableMap;
}
