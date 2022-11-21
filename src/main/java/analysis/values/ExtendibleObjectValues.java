package analysis.values;

/**
 * Object values that can have subclasses
 */
public class ExtendibleObjectValues extends ObjectWithNotNullValues {
    @Override
    public ObjectWithNotNullValues copy() {
        return null;
    }
}
