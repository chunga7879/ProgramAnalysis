package analysis.values;

public class IntegerRange extends PossibleValues {
    private int min;
    private int max;

    public IntegerRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public PossibleValues add(IntegerRange other) {
        return new IntegerRange(
                this.min + other.min,
                this.max + other.max
        );
    }
}
