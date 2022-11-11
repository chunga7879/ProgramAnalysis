import javax.validation.constraints.NotNull;

public class MethodCallExample {

    boolean doSomething(@NotNull String a) {
        return true;
    }

    // Passing null to a @NotNull annotated method
    boolean passNullToMethod(String str) {
        doSomething(null);
        // For 'str.contains', the '@NotNull s' is added by IntelliJ
        // will need to figure out if we can add it ourselves
        return str.contains(null);
    }
}
