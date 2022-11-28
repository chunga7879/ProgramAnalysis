package demo;

public class ArithmeticExceptionExample {

    // throws ArithmeticException if y is 0
    int divisionByZero(int x, int y) {
        switch (x) {
            default -> {
                y = 2;
            }
        }
        return x / y;
    }
}
