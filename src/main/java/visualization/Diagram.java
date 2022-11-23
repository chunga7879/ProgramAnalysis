package visualization;

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
}
