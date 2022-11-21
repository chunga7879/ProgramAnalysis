package analysis.values;

/**
 * Values that are primitives
 */
public abstract class PrimitiveValues extends PossibleValues {
    @Override
    public final boolean canBeNull() {
        return false;
    }
}
