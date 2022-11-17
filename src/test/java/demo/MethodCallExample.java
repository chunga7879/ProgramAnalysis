package demo;

import javax.validation.constraints.NotNull;

public class MethodCallExample {

    /**
     * Method that will do something
     *
     * @param a string to do something
     * @throws ArithmeticException
     * @throws java.io.IOException
     * @throws java.nio.BufferOverflowException
     */
    boolean doSomething(@NotNull String a) {
        return true;
    }

    // Passing null to a @NotNull annotated method
    boolean passNullToMethod(String str) {
        doSomething(null);
        // For built-in functions, requires different handling for
        // retrieving JavaDocs runtime exceptions
        Math.addExact(1, 2);
        // For 'str.contains', the '@NotNull s' is added by IntelliJ
        // will need to figure out if we can add it ourselves
        return str.contains(null);
    }
}
