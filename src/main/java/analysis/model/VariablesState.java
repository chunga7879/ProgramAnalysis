package analysis.model;

import analysis.values.EmptyValue;
import analysis.values.PossibleValues;
import analysis.values.visitor.IntersectVisitor;
import analysis.values.visitor.MergeVisitor;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;

import java.util.HashMap;
import java.util.Map;

public class VariablesState {
    private final Map<Node, PossibleValues> variableMap;
    private boolean isDomainEmpty;

    public VariablesState() {
        this.variableMap = new HashMap<>();
        this.isDomainEmpty = false;
    }

    public VariablesState(Map<Node, PossibleValues> variableMap, boolean isDomainEmpty) {
        this.variableMap = new HashMap<>(variableMap);
        this.isDomainEmpty = isDomainEmpty;
    }

    public static VariablesState createEmpty() {
        VariablesState state = new VariablesState();
        state.setDomainEmpty();
        return state;
    }

    public void setVariable(VariableDeclarator declaratorNode, PossibleValues value) {
        setVariableHelper(declaratorNode, value);
    }

    public void setVariable(Parameter parameter, PossibleValues value) {
        setVariableHelper(parameter, value);
    }

    private void setVariableHelper(Node node, PossibleValues value) {
        if (value.isEmpty()) isDomainEmpty = true;
        variableMap.put(node, value);
    }

    public PossibleValues getVariable(VariableDeclarator declaratorNode) {
        return getVariableHelper(declaratorNode);
    }

    public PossibleValues getVariable(Parameter parameter) {
        return getVariableHelper(parameter);
    }

    private PossibleValues getVariableHelper(Node node) {
        if (isDomainEmpty || !variableMap.containsKey(node)) return new EmptyValue();
        return variableMap.get(node);
    }

    /**
     * Set the domain to be empty
     */
    public void setDomainEmpty() {
        isDomainEmpty = true;
    }

    /**
     * @return True if the domain is empty, false otherwise
     */
    public boolean isDomainEmpty() {
        return isDomainEmpty;
    }

    /**
     * Merge this state with another state and take sum of both its domains
     * @param other Other state to merge
     * @return Merged copy of the state
     */
    public VariablesState mergeCopy(MergeVisitor mergeVisitor, VariablesState other) {
        VariablesState copy = this.copy();
        copy.merge(mergeVisitor, other);
        return copy;
    }

    /**
     * Combine this state with another state and take only the domains that intersect
     * @param other Other state to intersect
     * @return Intersection between this and the other
     */
    public VariablesState intersectCopy(IntersectVisitor intersectVisitor, VariablesState other) {
        VariablesState copy = this.copy();
        copy.intersect(intersectVisitor, other);
        return copy;
    }

    /**
     * Merge another state into this state
     * @param mergeVisitor Visitor to perform merge
     * @param other Other state to merge
     */
    public void merge(MergeVisitor mergeVisitor, VariablesState other) {
        if (!this.isDomainEmpty() && !other.isDomainEmpty()) {
            for (Map.Entry<Node, PossibleValues> entry : this.variableMap.entrySet()) {
                if (other.variableMap.containsKey(entry.getKey())) {
                    PossibleValues otherValues = other.variableMap.get(entry.getKey());
                    PossibleValues mergedValues = entry.getValue().acceptAbstractOp(mergeVisitor, otherValues);
                    this.setVariableHelper(entry.getKey(), mergedValues);
                }
            }
            for (Map.Entry<Node, PossibleValues> entry : other.variableMap.entrySet()) {
                if (!this.variableMap.containsKey(entry.getKey())) {
                    this.setVariableHelper(entry.getKey(), entry.getValue());
                }
            }
        } else if (this.isDomainEmpty() && other.isDomainEmpty()) {
            clear();
            this.setDomainEmpty();
        } else if (this.isDomainEmpty()) {
            copyValuesFrom(other);
        }
    }

    /**
     * Intersect this state with another state and leave only the domains that intersect
     * @param other Other state to intersect
     */
    public void intersect(IntersectVisitor intersectVisitor, VariablesState other) {
        if (this.isDomainEmpty() || other.isDomainEmpty) {
            clear();
            this.setDomainEmpty();
        } else {
            for (Map.Entry<Node, PossibleValues> entry : this.variableMap.entrySet()) {
                if (other.variableMap.containsKey(entry.getKey())) {
                    PossibleValues otherValues = other.variableMap.get(entry.getKey());
                    PossibleValues intersectValues = entry.getValue().acceptAbstractOp(intersectVisitor, otherValues);
                    this.setVariableHelper(entry.getKey(), intersectValues);
                }
            }
        }
    }

    /**
     * Clear the states
     */
    public void clear() {
        this.variableMap.clear();
        this.isDomainEmpty = false;
    }

    /**
     * Copy the state (shallow copies the map)
     * @return Copy of the state
     */
    public VariablesState copy() {
        // TODO: when mutable objects are added, need to handle copying properties
        return new VariablesState(this.variableMap, this.isDomainEmpty);
    }

    /**
     * Copy state from another state
     * @param other State to copy the variable map from
     */
    public void copyValuesFrom(VariablesState other) {
        clear();
        this.variableMap.putAll(other.variableMap);
        this.isDomainEmpty = other.isDomainEmpty;
    }

    public String toFormattedString() {
        if (this.isDomainEmpty()) return "empty domain";
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<Node, PossibleValues> entry : this.variableMap.entrySet()) {
            Node var = entry.getKey();
            String name;
            if (var instanceof VariableDeclarator decVar) {
                name = decVar.getNameAsString();
            } else if (var instanceof Parameter paramVar) {
                name = paramVar.getNameAsString();
            } else {
                name = var.toString();
            }
            String val = entry.getValue().toFormattedString();
            if (!first) sb.append(", ");
            sb.append(name).append(" -> ").append(val);
            first = false;
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VariablesState that)) return false;

        if (isDomainEmpty != that.isDomainEmpty) return false;
        return variableMap.equals(that.variableMap);
    }

    @Override
    public int hashCode() {
        int result = variableMap.hashCode();
        result = 31 * result + (isDomainEmpty ? 1 : 0);
        return result;
    }
}
