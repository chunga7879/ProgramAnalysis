package visualization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DiagramTest {
    final private String outputLocation = "src/test/java/visualization/outputs/";
    Diagram diagram;
    @BeforeEach
    public void beforeEach() {
        diagram = new Diagram();
    }

    @Test
    public void createSimpleDiagram() {
        DiagramNode node = new DiagramNode("int b = 1", Error.NONE, "");
        diagram.startDiagram();
        diagram.addNode(node);
        diagram.endDiagram();
        diagram.createDiagramPNG(outputLocation + "SimpleDiagram.png");
    }
}
