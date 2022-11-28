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
        DiagramNode node = new DiagramNode("int b = 1", ErrorType.NONE, null);
        diagram.addStartDiagramNode();
        diagram.addNode(node);
        diagram.addEndDiagramNode();
        diagram.createDiagramPNG(outputLocation + "SimpleDiagram.png");
    }

    @Test
    public void createSimpleErrorDiagram() {
        DiagramNode methodCall = new DiagramNode("methodCall(Object c)", ErrorType.NONE, null);
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ErrorType.POTENTIAL, "NullPointerException : c.toString()"));
        List<Error> errors2 = new ArrayList<>();
        errors2.add(new Error(ErrorType.DEFINITE, "ArithmeticException : 2/0"));
        DiagramNode potentialError = new DiagramNode("int b = c.toString()", ErrorType.POTENTIAL, errors);
        DiagramNode error = new DiagramNode("int a = 2/0;", ErrorType.DEFINITE, errors2);
        diagram.addStartDiagramNode();
        diagram.addNode(methodCall);
        diagram.addNode(potentialError);
        diagram.addNode(error);
        diagram.addEndDiagramNode();
        diagram.createDiagramPNG(outputLocation + "SimpleErrorDiagram.png");
    }

    @Test
    public void createIfConditional() {
        DiagramNode methodCall = new DiagramNode("methodCall(Object c)", ErrorType.NONE, null);
        DiagramNode ifCondition = new DiagramNode("x > 10", ErrorType.NONE, null);
        DiagramNode statement = new DiagramNode("print(x);", ErrorType.NONE, null);
        List<Error> error = new ArrayList<>();
        error.add(new Error(ErrorType.POTENTIAL, "Placeholder"));
        DiagramNode potentialError = new DiagramNode("print(x);", ErrorType.POTENTIAL, error);
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
        DiagramNode methodCall = new DiagramNode("methodCall(Object c)", ErrorType.NONE, null);
        DiagramNode whileCondition = new DiagramNode("x > 10", ErrorType.NONE, null);
        DiagramNode statement = new DiagramNode("print(x);", ErrorType.NONE, null);
        List<Error> error = new ArrayList<>();
        error.add(new Error(ErrorType.POTENTIAL, "Placeholder"));
        DiagramNode potentialError = new DiagramNode("print(x);", ErrorType.POTENTIAL, error);
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
        DiagramNode methodCall = new DiagramNode("methodCall(Object c)", ErrorType.NONE, null);
        DiagramNode doWhileCondition = new DiagramNode("x > 10", ErrorType.NONE, null);
        DiagramNode statement = new DiagramNode("print(x);", ErrorType.NONE, null);
        List<Error> error = new ArrayList<>();
        error.add(new Error(ErrorType.POTENTIAL, "Placeholder"));
        DiagramNode potentialError = new DiagramNode("print(x);", ErrorType.POTENTIAL, error);
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
        DiagramNode methodCall = new DiagramNode("methodCall(Object c)", ErrorType.NONE, null);
        DiagramNode statement = new DiagramNode("print(x);", ErrorType.NONE, null);
        List<Error> error = new ArrayList<>();
        error.add(new Error(ErrorType.POTENTIAL, "Placeholder"));
        DiagramNode potentialError = new DiagramNode("print(x);", ErrorType.POTENTIAL, error);
        diagram.addStartDiagramNode();
        diagram.addNode(methodCall);
        List<DiagramNode> initialization = new ArrayList<>();
        initialization.add(new DiagramNode("int i = 0", ErrorType.NONE, null));
        DiagramNode node = new DiagramNode("int i < 10", ErrorType.NONE, null);
        diagram.addForStartNode(initialization, node);
        diagram.addNode(statement);
        diagram.addNode(potentialError);
        List<DiagramNode> update = new ArrayList<>();
        initialization.add(new DiagramNode("i++", ErrorType.NONE, null));
        diagram.addForEndNode(update);
        diagram.addNode(statement);
        diagram.addEndDiagramNode();
        diagram.createDiagramPNG(outputLocation + "ForLoop.png");
    }

    @Test
    public void createThrowStatement() {
        DiagramNode methodCall = new DiagramNode("methodCall(Object c)", ErrorType.NONE, null);
        DiagramNode ifCondition = new DiagramNode("x > 10", ErrorType.NONE, null);
        DiagramNode statement = new DiagramNode("print(x);", ErrorType.NONE, null);
        List<Error> error = new ArrayList<>();
        error.add(new Error(ErrorType.POTENTIAL, "Placeholder"));
        DiagramNode potentialError = new DiagramNode("print(x);", ErrorType.POTENTIAL, error);
        List<Error> definiteError = new ArrayList<>();
        definiteError.add(new Error(ErrorType.DEFINITE, "Exception : throw new Exception()"));
        DiagramNode throwStatement = new DiagramNode("throw new Exception();", ErrorType.DEFINITE, definiteError);
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
        DiagramNode methodCall = new DiagramNode("methodCall(Object c)", ErrorType.NONE, null);
        DiagramNode statement = new DiagramNode("print(x);", ErrorType.NONE, null);
        List<Error> error = new ArrayList<>();
        error.add(new Error(ErrorType.POTENTIAL, "Placeholder"));
        DiagramNode potentialError = new DiagramNode("print(x);", ErrorType.POTENTIAL, error);
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
