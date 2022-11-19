package analysis.model;

/**
 * State of the analysis
 */
public class AnalysisState {
    private VariablesState variablesState;
    // TODO: add visualization object
    public AnalysisState(VariablesState variablesState) {
        this.variablesState = variablesState;
    }

    public VariablesState getVariablesState() {
        return variablesState;
    }
}
