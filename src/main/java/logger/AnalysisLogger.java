package logger;

import analysis.model.AnalysisError;
import analysis.model.VariablesState;
import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;

import java.util.List;

/**
 * Logger for viewing the state of the analysis
 */
public final class AnalysisLogger {

    /**
     * Log the state of the arg at a node
     */
    public static void log(Node n, VariablesState arg) {
        log(n, arg, null);
    }

    /**
     * Log the state of the arg and errors at a node
     */
    public static void log(Node n, VariablesState arg, List<AnalysisError> errors) {
        log(n, arg.toFormattedString());
        if (errors != null && !errors.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Errors: ");
            boolean first = true;
            for (AnalysisError error : errors) {
                if (!first) sb.append(", ");
                sb.append("'").append(error.getMessage()).append("'");
                first = false;
            }
            log(n, sb.toString());
        }
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