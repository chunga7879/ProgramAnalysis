package logger;

import analysis.model.AnalysisError;
import analysis.model.VariablesState;
import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;

import java.util.Collection;

/**
 * Logger for viewing the state of the analysis
 */
public final class AnalysisLogger {
    private static boolean doLog = false;

    public static void setLog(boolean doLog) {
        AnalysisLogger.doLog = doLog;
    }

    /**
     * Log the state of the arg at a node
     */
    public static void log(Node n, VariablesState arg) {
        if (!doLog) return;
        log(n, arg, null);
    }

    /**
     * Log the state of the arg and errors at a node
     */
    public static void log(Node n, VariablesState arg, Collection<AnalysisError> errors) {
        if (!doLog) return;
        log(n, arg.toFormattedString());
        logErrors(n, errors);
    }

    /**
     * Log a message at a node
     */
    public static void logFormat(Node n, String format, Object... args) {
        if (!doLog) return;
        log(n, String.format(format, args));
    }

    /**
     * Log a message at a node
     */
    public static void log(Node n, String message) {
        if (!doLog) return;
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
    public static void logEndFormat(Node n, String format, Object... arg) {
        if (!doLog) return;
        logEnd(n, String.format(format, arg));
    }


    /**
     * Log the end of the control statement
     */
    public static void logEnd(Node n, String message) {
        if (!doLog) return;
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

    /**
     * Log errors at node
     */
    public static void logErrors(Node n, Collection<AnalysisError> errors) {
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
}
