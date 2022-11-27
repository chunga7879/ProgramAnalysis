package visualization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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
        diagram.addStartDiagramNode();
        diagram.addNode(node);
        diagram.addEndDiagramNode();
        diagram.createDiagramPNG(outputLocation + "SimpleDiagram.png");
    }

    @Test
    public void createSimpleErrorDiagram() {
        DiagramNode methodCall = new DiagramNode("methodCall(Object c)", Error.NONE, "");
        DiagramNode potentialError = new DiagramNode("int b = c.toString()", Error.POTENTIAL, "NullPointerException : c.toString()");
        DiagramNode error = new DiagramNode("int a = 2/0;", Error.DEFINITE, "ArithmeticException : 2/0");
        diagram.addStartDiagramNode();
        diagram.addNode(methodCall);
        diagram.addNode(potentialError);
        diagram.addNode(error);
        diagram.addEndDiagramNode();
        diagram.createDiagramPNG(outputLocation + "SimpleErrorDiagram.png");
    }

    @Test
    public void createIfConditional() {
        DiagramNode methodCall = new DiagramNode("methodCall(Object c)", Error.NONE, "");
        DiagramNode ifCondition = new DiagramNode("x > 10", Error.NONE, "");
        DiagramNode statement = new DiagramNode("print(x);", Error.NONE, "");
        DiagramNode potentialError = new DiagramNode("print(x);", Error.POTENTIAL, "Placeholder");
        diagram.addStartDiagramNode();
        diagram.addNode(methodCall);
        diagram.addIfThenStartNode(ifCondition);
        diagram.addNode(statement);
        diagram.addIfElseNode();
        diagram.addNode(potentialError);
        diagram.addIfEndNode();
        diagram.addEndDiagramNode();
        diagram.createDiagramPNG(outputLocation + "IfConditional.png");
    }

    @Test
    public void createWhileLoop() {
        DiagramNode methodCall = new DiagramNode("methodCall(Object c)", Error.NONE, "");
        DiagramNode whileCondition = new DiagramNode("x > 10", Error.NONE, "");
        DiagramNode statement = new DiagramNode("print(x);", Error.NONE, "");
        DiagramNode potentialError = new DiagramNode("print(x);", Error.POTENTIAL, "Placeholder");
        diagram.addStartDiagramNode();
        diagram.addNode(methodCall);
        diagram.addWhileForEachConditionalStartNode(whileCondition);
        diagram.addNode(statement);
        diagram.addNode(potentialError);
        diagram.addWhileForEachEndNode();
        diagram.addNode(statement);
        diagram.addEndDiagramNode();
        diagram.createDiagramPNG(outputLocation + "WhileLoop.png");
    }

    @Test
    public void createDoWhileLoop() {
        DiagramNode methodCall = new DiagramNode("methodCall(Object c)", Error.NONE, "");
        DiagramNode doWhileCondition = new DiagramNode("x > 10", Error.NONE, "");
        DiagramNode statement = new DiagramNode("print(x);", Error.NONE, "");
        DiagramNode potentialError = new DiagramNode("print(x);", Error.POTENTIAL, "Placeholder");
        diagram.addStartDiagramNode();
        diagram.addNode(methodCall);
        diagram.addDoWhileStartNode();
        diagram.addNode(statement);
        diagram.addNode(potentialError);
        diagram.addDoWhileConditionalEndNode(doWhileCondition);
        diagram.addNode(statement);
        diagram.addEndDiagramNode();
        diagram.createDiagramPNG(outputLocation + "DoWhileLoop.png");
    }

    @Test
    public void createForLoop() {
        DiagramNode methodCall = new DiagramNode("methodCall(Object c)", Error.NONE, "");
        DiagramNode statement = new DiagramNode("print(x);", Error.NONE, "");
        DiagramNode potentialError = new DiagramNode("print(x);", Error.POTENTIAL, "Placeholder");
        diagram.addStartDiagramNode();
        diagram.addNode(methodCall);
        List<String> initialization = new ArrayList<>();
        initialization.add("int i = 1");
        diagram.addForStartNode(initialization, "int i < 10");
        diagram.addNode(statement);
        diagram.addNode(potentialError);
        List<String> update = new ArrayList<>();
        initialization.add("i++");
        diagram.addForEndNode(update);
        diagram.addNode(statement);
        diagram.addEndDiagramNode();
        diagram.createDiagramPNG(outputLocation + "ForLoop.png");
    }

    @Test
    public void createThrowStatement() {
        DiagramNode methodCall = new DiagramNode("methodCall(Object c)", Error.NONE, "");
        DiagramNode ifCondition = new DiagramNode("x > 10", Error.NONE, "");
        DiagramNode statement = new DiagramNode("print(x);", Error.NONE, "");
        DiagramNode potentialError = new DiagramNode("print(x);", Error.POTENTIAL, "Placeholder");
        DiagramNode throwStatement = new DiagramNode("throw new Exception();", Error.DEFINITE, "Exception : throw new Exception()");
        diagram.addStartDiagramNode();
        diagram.addNode(methodCall);
        diagram.addIfThenStartNode(ifCondition);
        diagram.addNode(statement);
        diagram.addIfElseNode();
        diagram.addNode(potentialError);
        diagram.addThrowStatementNode(throwStatement);
        diagram.addIfEndNode();
        diagram.addEndDiagramNode();
        diagram.createDiagramPNG(outputLocation + "ThrowStatement.png");
    }

    @Test
    public void createSwitchStatement() {
        DiagramNode methodCall = new DiagramNode("methodCall(Object c)", Error.NONE, "");
        DiagramNode statement = new DiagramNode("print(x);", Error.NONE, "");
        DiagramNode potentialError = new DiagramNode("print(x);", Error.POTENTIAL, "Placeholder");
        diagram.addStartDiagramNode();
        diagram.addNode(methodCall);
        diagram.addSwitchConditionalStartNode("switch (x)");
        diagram.addSwitchCaseNode("case 10:");
        diagram.addNode(potentialError);
        diagram.addSwitchCaseNode("case 5:");
        diagram.addNode(statement);
        diagram.addSwitchDefaultNode();
        diagram.addNode(statement);
        diagram.addSwitchEndNode();
        diagram.addEndDiagramNode();
        diagram.createDiagramPNG(outputLocation + "SwitchStatement.png");
    }
}
