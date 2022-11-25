package visualization;

import net.sourceforge.plantuml.SourceStringReader;

import java.io.*;

public class Diagram {
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
        switch (node.error()) {
            case POTENTIAL -> addErrorNode(node.statement(), node.errorDescription(), true);
            case DEFINITE -> addErrorNode(node.statement(), node.errorDescription(), false);
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
    public void addWhileConditionalStartNode(DiagramNode node) {
        String conditional = "while (" + node.statement() + ")\n";
//        DiagramNode conditionalNode = new DiagramNode(conditional, node.error(), node.errorDescription());
//        addNode(conditionalNode);
        diagramString.append(conditional);
    }

    public void addWhileEndNode() {
        diagramString.append("endwhile\n");
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
