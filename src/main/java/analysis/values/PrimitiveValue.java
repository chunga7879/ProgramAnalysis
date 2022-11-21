package analysis.values;

/**
 * Values that are primitives
 */
public abstract class PrimitiveValue extends PossibleValues {
    @Override
    public final boolean canBeNull() {
        return false;
    }
}
