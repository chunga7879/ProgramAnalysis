package examples;

public class ForLoopExample {
    // For loop, with fixed iterations
    void testForLoopFixed() {
        int a = 10;
        for (int i = 0; i < 10; i = i + 1) {
            a = i + 10;
        }
        System.out.println(a);
    }

    // For loop, with non-fixed iterations
    void testForLoopAny(int x) {
        int a = 10;
        for (int i = 0; i < x; i = i + 1) {
            a = i + 5;
        }
        System.out.println(a);
    }

    // For loop, with infinite loops
    void testForLoopInfinite(int x) {
        int a = 10;
        for (int i = 0; i < x; i = i - 1) {
            a = i + 5;
        }
        System.out.println(a);
    }

    // For loop, with loops inside loops
    void testForLoopInside(int x) {
        int a = 10;
        for (int i = 0; i < 100; i = i + 1) {
            for (int j = 0; j < 100; j = j + 1) {
                for (int k = 0; k < 100; k = k + 1) {
                    a = i + j + k + 5;
                }
            }
        }
        System.out.println(a);
    }
}
