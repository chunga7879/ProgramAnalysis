package analysis.model;

/**
 * Store a state for when the conditional is true and another for when it is false
 */
public class ConditionStates {
    public VariablesState trueState;
    public VariablesState falseState;

    public ConditionStates(VariablesState trueState, VariablesState falseState) {
        this.trueState = trueState;
        this.falseState = falseState;
    }

    public VariablesState getTrueState() {
        return trueState;
    }

    public VariablesState getFalseState() {
        return falseState;
    }
}
