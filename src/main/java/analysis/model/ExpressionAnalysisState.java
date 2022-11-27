package analysis.model;

import java.util.ArrayList;
import java.util.List;

/**
 * State of the analysis on an expression
 */
public class ExpressionAnalysisState {
    private VariablesState variablesState;
    private List<AnalysisError> errors;

    public ExpressionAnalysisState(VariablesState variablesState) {
        this.variablesState = variablesState;
        this.errors = new ArrayList<>();
    }

    public ExpressionAnalysisState(VariablesState variablesState, List<AnalysisError> errors) {
        this.variablesState = variablesState;
        this.errors = errors;
    }

    public VariablesState getVariablesState() {
        return variablesState;
    }

    public List<AnalysisError> getErrors() {
        return errors;
    }

    public void setVariablesState(VariablesState variablesState) {
        this.variablesState = variablesState;
    }

    public void setErrors(List<AnalysisError> errors) {
        this.errors = errors;
    }

    /**
     * Add an error to the state
     */
    public void addError(AnalysisError error) {
        this.errors.add(error);
    }
}
