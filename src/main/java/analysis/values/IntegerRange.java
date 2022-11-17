package analysis.values;

import utils.MathUtil;

public class IntegerRange extends PossibleValues {
    private final int min;
    private final int max;

    public IntegerRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    @Override
    public PossibleValues merge(PossibleValues other) {
        return other.mergeTo(this);
    }

    @Override
    protected IntegerRange mergeTo(IntegerRange other) {
        return new IntegerRange(
                Math.min(this.min, other.min),
                Math.max(this.max, other.max)
        );
    }

    @Override
    public PossibleValues add(PossibleValues other) {
        return other.addTo(this);
    }

    @Override
    protected IntegerRange addTo(IntegerRange target) {
        return new IntegerRange(
                MathUtil.addToLimit(target.min, this.min),
                MathUtil.addToLimit(target.max, this.max)
        );
    }

    @Override
    public PossibleValues subtract(PossibleValues target) {
        return target.subtractFrom(this);
    }

    @Override
    protected IntegerRange subtractFrom(IntegerRange target) {
        return new IntegerRange(
                MathUtil.subtractToLimit(target.min, this.max),
                MathUtil.subtractToLimit(target.max, this.min)
        );
    }
}
