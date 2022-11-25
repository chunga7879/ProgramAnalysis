package analysis.model;

import visualization.Diagram;

/**
 * State of the analysis
 */
public class AnalysisState {
    private VariablesState variablesState;

    public Diagram diagram;

    public AnalysisState(VariablesState variablesState) {
        this.variablesState = variablesState;
    }

    public VariablesState getVariablesState() {
        return variablesState;
    }
}
