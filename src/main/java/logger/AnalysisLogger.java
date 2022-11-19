package logger;

import analysis.model.VariablesState;
import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;

/**
 * Logger for viewing the state of the analysis
 */
public final class AnalysisLogger {
    /**
     * Log the state of the arg at a node
     */
    public static void log(Node n, VariablesState arg) {
        log(n, arg.toFormattedString());
    }

    /**
     * Log a message at a node
     */
    public static void log(Node n, String message) {
        String lineCount = "?";
        if (n.getRange().isPresent()) {
            Range range = n.getRange().get();
            lineCount = range.begin.line + "";
        }
        String line = getFirstLine(n.toString());
        line = line.substring(0, Math.min(20, line.length()));
        System.out.printf("[%2s] %-20s | %s\n", lineCount, line, message);
    }

    /**
     * Log the end of the control statement
     */
    public static void logEnd(Node n, String message) {
        String lineCount = "?";
        if (n.getRange().isPresent()) {
            Range range = n.getRange().get();
            lineCount = range.end.line + "";
        }
        String line = "}";
        System.out.printf("[%2s] %-20s | %s\n", lineCount, line, message);
    }

    /**
     * Get first line of the string
     */
    private static String getFirstLine(String str) {
        String[] split = str.split("\n");
        if (split.length < 1) return "";
        return split[0];
    }
}
