package analysis.model;

import java.util.HashSet;
import java.util.Set;

/**
 * State for end of statement/block (tracking early returns/breaks/continues)
 */
public class EndState {
    public Set<VariablesState> breakStates;
    public Set<VariablesState> continueStates;

    public EndState() {
        this.breakStates = new HashSet<>();
        this.continueStates = new HashSet<>();
    }

    public void add(EndState other) {
        if (other == null) return;
        this.breakStates.addAll(other.breakStates);
        this.continueStates.addAll(other.continueStates);
    }

    public void addBreakState(VariablesState state) {
        this.breakStates.add(state);
    }

    public void addContinueState(VariablesState state) {
        this.continueStates.add(state);
    }

    public Set<VariablesState> popBreakStates() {
        Set<VariablesState> states = this.breakStates;
        this.breakStates = new HashSet<>();
        return states;
    }

    public Set<VariablesState> popContinueStates() {
        Set<VariablesState> states = this.continueStates;
        this.continueStates = new HashSet<>();
        return states;
    }
}
