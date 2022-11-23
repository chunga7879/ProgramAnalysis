package visualization;

import jdk.jshell.Diag;
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

    @Test
    public void createSimpleErrorDiagram() {
        DiagramNode methodCall = new DiagramNode("methodCall(Object c)", Error.NONE, "");
        DiagramNode potentialError = new DiagramNode("int b = c.toString()", Error.POTENTIAL, "NullPointerException : c.toString()");
        DiagramNode error = new DiagramNode("int a = 2/0;", Error.DEFINITE, "ArithmeticException : 2/0");
        diagram.startDiagram();
        diagram.addNode(methodCall);
        diagram.addNode(potentialError);
        diagram.addNode(error);
        diagram.endDiagram();
        diagram.createDiagramPNG(outputLocation + "SimpleErrorDiagram.png");
    }
}
