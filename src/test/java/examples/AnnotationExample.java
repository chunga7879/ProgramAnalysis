package examples;

import javax.validation.constraints.Positive;

public class AnnotationExample {
    public void exampleWithoutAnnotation(int i) {
        if (false) {
            int y = 2 / 0; // Shouldn't produce an error in the visualization
        }
        int z = 100 / i;
        int[] n = new int[i];
    }

    public void exampleWithAnnotation(@Positive int i) {
        if (false) {
            int y = 2 / 0; // Shouldn't produce an error in the visualization
        }
        int z = 100 / i;
        int[] n = new int[i];
    }
}
