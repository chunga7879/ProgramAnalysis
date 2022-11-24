package analysis.model;

import java.util.List;

/**
 * Store a state for when the conditional is true and another for when it is false
 */
public class ConditionStates {
    private VariablesState trueState;
    private VariablesState falseState;
    private List<AnalysisError> errors;

    public ConditionStates(VariablesState trueState, VariablesState falseState, List<AnalysisError> errors) {
        this.trueState = trueState;
        this.falseState = falseState;
        this.errors = errors;
    }

    public VariablesState getTrueState() {
        return trueState;
    }

    public VariablesState getFalseState() {
        return falseState;
    }

    public List<AnalysisError> getErrors() {
        return errors;
    }

    public void setErrors(List<AnalysisError> errors) {
        this.errors = errors;
    }

    public void addErrors(List<AnalysisError> errors) {
        this.errors.addAll(errors);
    }
}
