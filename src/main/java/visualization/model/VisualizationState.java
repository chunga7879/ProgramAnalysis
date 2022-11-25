package visualization.model;

import analysis.model.AnalysisError;
import com.github.javaparser.ast.Node;
import visualization.Diagram;
import visualization.visitor.VisualizationVisitor;

import java.util.Map;
import java.util.Set;

public class VisualizationState {
    public Diagram diagram;
    private Map<Node, Set<AnalysisError>> errorMap;

    public VisualizationState(Map<Node, Set<AnalysisError>> errorMap) {
        diagram = new Diagram();
        this.errorMap = errorMap;
    }

    public VisualizationState(Diagram diagram, Map<Node, Set<AnalysisError>> errorMap) {
        this.diagram = diagram;
        this.errorMap = errorMap;
    }
}
