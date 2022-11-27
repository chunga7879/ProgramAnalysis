package utils;

public final class MathUtil {
    /**
     * Add a + b without overflows and limits the final value in valid range of int
     */
    public static int addToLimit(int a, int b) {
        long result = (long)a + (long)b;
        return (int)limitToIntRange(result);
    }

    public static int divideToLimit(int a, int b) {
        long result = (long)a / (long)b;
        return (int)limitToIntRange(result);
    }

    /**
     * Multiply a * b without overflows and limits the final value in valid range of int
     */
    public static int multiplyToLimit(int a, int b) {
        long result = (long)a * (long)b;
        return (int)limitToIntRange(result);
    }

    /**
     * Subtract a - b without overflows and limits the final value in valid range of int
     */
    public static int subtractToLimit(int a, int b) {
        long result = (long)a - (long)b;
        return (int)limitToIntRange(result);
    }

    /**
     * Flip sign of 'a' without overflows and limits the final value in valid range of int
     */
    public static int flipSignToLimit(int a) {
        long result = -((long)a);
        return (int)limitToIntRange(result);
    }

    /**
     * Limits 'a' between Integer.MIN_VALUE and Integer.MAX_VALUE
     */
    private static long limitToIntRange(long a) {
        if (a > (long)Integer.MAX_VALUE) return Integer.MAX_VALUE;
        if (a < (long)Integer.MIN_VALUE) return Integer.MIN_VALUE;
        return a;
    }
}
