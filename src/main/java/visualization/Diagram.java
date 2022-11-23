package visualization;

import net.sourceforge.plantuml.SourceStringReader;

import java.io.*;
import java.util.List;

public class Diagram {
    private StringBuilder diagramString;

    public Diagram() {
        diagramString = new StringBuilder();
    }

    public void startDiagram() {
        diagramString.append("@startuml\n");
        diagramString.append("start\n");
    }

    public void endDiagram() {
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

    public void addIfNode(String conditional, List<String> trueBranch, List<String> falseBranch) {
        diagramString.append("if (" + conditional + ") then (true)\n");
        for (String statement : trueBranch) {
            addStatementNode(statement);
        }
        diagramString.append("else (false)\n");

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
        diagramString.append("note right:" + error);
    }

    /**
     * Creates PNG of diagram
     * NOTE: PNG creation referenced from: https://plantuml.com/api
     * @param pngName Location where to store PNG created
     */
    public void createDiagramPNG(String pngName) {
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
