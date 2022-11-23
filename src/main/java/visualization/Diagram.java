package visualization;

import net.sourceforge.plantuml.SourceStringReader;

import java.io.*;

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
        diagramString.append("@enduml\n");
    }

    public String getDiagramString() {
        return diagramString.toString();
    }

    public void addStatementNode(String statement) {
        diagramString.append(":" + statement + ";\n");
    }

    /**
     * Adds a node to the plantUML diagram, where the statement has an error
     * @param statement Statement that causes error
     * @param error Error description; format should be [ERROR NAME]:[EXPRESSION CAUSING ERROR]
     * @param potentialError If true, error is potential/dependant on runtime values, if false, error is definite to occur
     */
    public void addErrorNode(String statement, String error, boolean potentialError) {
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
            String desc = sourceStringReader.generateImage(png);
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println(fileNotFoundException);
            System.out.println("Error occurred when creating output stream.");
        } catch (IOException io) {
            // TODO: error handling
            System.out.println(io);
            System.out.println("Error occurred when creating PNG from SourceStringReader");
        }
    }
}
