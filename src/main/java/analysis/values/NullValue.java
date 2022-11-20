package analysis.values;

/**
 * Value that is null
 */
public class NullValue extends PossibleValues {
    public boolean canBeNull() {
        return true;
    }
}
