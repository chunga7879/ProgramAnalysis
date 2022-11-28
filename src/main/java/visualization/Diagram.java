package visualization;

import net.sourceforge.plantuml.SourceStringReader;

import java.io.*;
import java.util.List;

public class Diagram {

    private final String orangeColour = "#F28C28";
    private final String redColour = "#FF0000";
    private StringBuilder diagramString;

    public Diagram() {
        diagramString = new StringBuilder();
    }

    public void addStartDiagramNode() {
        diagramString.append("@startuml\n");
        diagramString.append("start\n");
    }

    public void addEndDiagramNode() {
        diagramString.append("stop\n");
        diagramString.append("@enduml");
    }

    public String getDiagramString() {
        return diagramString.toString();
    }

    public void addNode(DiagramNode node) {
        switch (node.errorType()) {
            case POTENTIAL -> addErrorNode(node.statement(), node.errors(), true);
            case DEFINITE -> addErrorNode(node.statement(), node.errors(), false);
            case NONE -> addStatementNode(node.statement());
            default -> throw new RuntimeException("Invalid type of DiagramNode");
        }
    }

    private void addStatementNode(String statement) {
        diagramString.append(":" + statement + ";\n");
    }

    /**
     * Adds a node to the diagram for a if conditional's condition
     * @param node Node that represents the diagram node. The node's statement should just be the conditional.
     */
    public void addIfThenStartNode(DiagramNode node) {
        String conditional = "if (" + node.statement() + ") then (true)\n";
//        DiagramNode conditionalNode = new DiagramNode(conditional, node.error(), node.errorDescription());
//        addNode(conditionalNode);
        diagramString.append(conditional);
    }

    public void addIfElseNode() {
        diagramString.append("else (false)\n");
    }

    public void addIfEndNode() {
        diagramString.append("endif\n");
    }

    /**
     * Adds a node to the diagram for a while loop's condition
     * @param node Node that represents the diagram node. The statement of the node should just be the conditional of the while loop.
     */
    public void addWhileForEachConditionalStartNode(DiagramNode node) {
        String conditional = "while (" + node.statement() + ") is (true)\n";
//        DiagramNode conditionalNode = new DiagramNode(conditional, node.error(), node.errorDescription());
//        addNode(conditionalNode);
        diagramString.append(conditional);
    }

    public void addWhileForEachEndNode() {
        diagramString.append("endwhile (false)\n");
    }

    public void addDoWhileStartNode() {
        diagramString.append("repeat\n");
    }

    /**
     * This adds the conditional for a do-while loop and also ends the loop in the diagram.
     * @param node Conditional on which the loop is running on
     */
    public void addDoWhileConditionalEndNode(DiagramNode node) {
        String conditional = "repeat while (" + node.statement() + ") is (true)\n";
        diagramString.append(conditional);
        diagramString.append("->false;\n");
    }

    /**
     * Adds start node for a for loop
     * @param initialization Initialization of for loop variable; ex. i = 1
     * @param condition Condition of for loop execution; ex. i < 10
     */
    public void addForStartNode(List<DiagramNode> initialization, DiagramNode condition) {
        for (DiagramNode node : initialization) {
            addNode(node);
        }
        // TODO: May have an error in conditional
//        DiagramNode conditional = new DiagramNode(condition, ErrorType.NONE, "");
        addWhileForEachConditionalStartNode(condition);
    }

    public void addSwitchConditionalStartNode(String condition) {
        diagramString.append("switch (" + condition + ")\n");
    }

    public void addSwitchCaseNode(String switchCase) {
        diagramString.append("case ( " + switchCase + " )\n");
    }

    public void addSwitchDefaultNode() {
        diagramString.append("case ( default )\n");
    }

    public void addSwitchEndNode() {
        diagramString.append("endswitch\n");
    }

    public void addThrowStatementNode(DiagramNode node) {
        addNode(node);
        // TODO: This is assuming that the exception we throw is not caught in the same method
        diagramString.append("stop\n");
    }

    /**
     * Adds end node for a for loop
     * @param updates How the conditional variable in the for loop is updated each iteration; ex. i++
     */
    public void addForEndNode(List<DiagramNode> updates) {
        for (DiagramNode update : updates) {
            addNode(update);
        }
        addWhileForEachEndNode();
    }

    public void addBreakStatementNode() {
        diagramString.append("break\n");
    }

    public void addContinueStatementNode() {
        diagramString.append("#ADD8E6:return to beginning of loop;\n");
        diagramString.append("detach\n");
    }


    /**
     * Adds a node to the plantUML diagram, where the statement has an error
     * @param statement Statement that causes error
     * @param error Error description; format should be [ERROR NAME]:[EXPRESSION CAUSING ERROR]
     * @param potentialError true: error is potential/dependant on runtime values; false: error is definite to occur regardless of value
     */
    private void addErrorNode(String statement, String error, boolean potentialError) {
        if (potentialError) {
            diagramString.append("#Orange");
        } else {
            diagramString.append("#Red");
        }
        addStatementNode(statement);
        diagramString.append("note right:" + error + "\n");
    }

    private void addErrorNode(String statement, List<Error> errors, boolean potentialError) {
        if (potentialError) {
            diagramString.append("#Orange");
        } else {
            diagramString.append("#Red");
        }
        addStatementNode(statement);
        addErrorInsightNote(errors);
    }

    private void addErrorInsightNote(List<Error> errors) {
        if (errors != null) {
            diagramString.append("note right\n");
            for (Error error : errors) {
                diagramString.append(addErrorDescription(error));
            }
            diagramString.append("endnote\n");
        }
    }

    private String addErrorDescription(Error error) {
        switch (error.errorType()) {
            case DEFINITE -> {
                return "<FONT COLOR=" + redColour + ">" + error.errorDescription() + "</FONT>\n";
            }
            case POTENTIAL -> {
                return "<FONT COLOR=" + orangeColour + ">" + error.errorDescription() + "</FONT>\n";
            }
            default -> {
                return "\n";
            }
        }
    }

    /**
     * Creates PNG of diagram
     * NOTE: PNG creation referenced from: https://plantuml.com/api
     * @param pngName Location where to store PNG created
     */
    public void createDiagramPNG(String pngName) {
        System.out.println("Creating diagram:\n" + getDiagramString());
        try {
            OutputStream png = new FileOutputStream(pngName);
            SourceStringReader sourceStringReader = new SourceStringReader(getDiagramString());
            sourceStringReader.outputImage(png);
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("Error occurred when creating output stream: " + fileNotFoundException);
        } catch (IOException io) {
            System.out.println("Error occurred when creating PNG from SourceStringReader: " + io);
        }
    }
}
