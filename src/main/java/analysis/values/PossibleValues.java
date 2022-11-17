package analysis.values;

public abstract class PossibleValues {

    /**
     * Merge values of this and other.
     * Note that merge is order-independent
     *
     * @param other Value we are merging with this
     * @return Result of merge
     */
    public PossibleValues merge(PossibleValues other) {
        return other.mergeTo(this);
    }

    /**
     * @see PossibleValues#add(PossibleValues)
     */
    protected PossibleValues mergeTo(PossibleValues other) {
        return new AnyValue();
    }

    /**
     * @see PossibleValues#add(PossibleValues)
     */
    protected PossibleValues mergeTo(IntegerRange other) {
        return new AnyValue();
    }

    /**
     * @see PossibleValues#add(PossibleValues)
     */
    protected PossibleValues mergeTo(StringValue other) {
        return new AnyValue();
    }

    /**
     * Add this + other and return value
     *
     * @param other Value being added to this
     * @return Result of addition
     */
    public PossibleValues add(PossibleValues other) {
        return other.addTo(this);
    }

    /**
     * Add target + this and return value
     *
     * @param target Value 'this' is being added to
     * @return Result of addition
     */
    protected PossibleValues addTo(PossibleValues target) {
        return new AnyValue();
    }

    /**
     * @see PossibleValues#addTo(PossibleValues)
     */
    protected PossibleValues addTo(IntegerRange target) {
        return new AnyValue();
    }

    /**
     * @see PossibleValues#addTo(PossibleValues)
     */
    protected PossibleValues addTo(StringValue target) {
        return new AnyValue();
    }

    /**
     * Subtract this - target and return value
     *
     * @param target Value being subtracted from this
     * @return Result of subtraction
     */
    public PossibleValues subtract(PossibleValues target) {
        return target.subtractFrom(this);
    }

    /**
     * Subtract target - this and return value
     *
     * @param target Value 'this' is being subtracted from
     * @return Result of subtraction
     */
    protected PossibleValues subtractFrom(PossibleValues target) {
        return new AnyValue();
    }

    /**
     * @see PossibleValues#subtractFrom(PossibleValues)
     */
    protected PossibleValues subtractFrom(IntegerRange target) {
        return new AnyValue();
    }
}
