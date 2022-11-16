package analysis.values;

import utils.MathUtil;

public class IntegerRange extends PossibleValues {
    private final int min;
    private final int max;

    public IntegerRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public PossibleValues merge(PossibleValues other) {
        return other.mergeTo(this);
    }

    @Override
    public PossibleValues mergeTo(IntegerRange other) {
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
    public IntegerRange addTo(IntegerRange firstValue) {
        return new IntegerRange(
                MathUtil.addToLimit(firstValue.min, this.min),
                MathUtil.addToLimit(firstValue.max, this.max)
        );
    }

    @Override
    public PossibleValues addTo(StringValue firstValue) {
        return firstValue.addTo(this);
    }

    @Override
    public PossibleValues subtract(PossibleValues other) {
        return other.subtractFrom(this);
    }

    @Override
    public IntegerRange subtractFrom(IntegerRange firstValue) {
        return new IntegerRange(
                MathUtil.subtractToLimit(firstValue.min, this.max),
                MathUtil.subtractToLimit(firstValue.max, this.min)
        );
    }
}
