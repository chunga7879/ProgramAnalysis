package visualization;

enum Error {
    POTENTIAL,
    DEFINITE,
    NONE
}

public record DiagramNode(String statement, Error error, String errorDescription) {
}
