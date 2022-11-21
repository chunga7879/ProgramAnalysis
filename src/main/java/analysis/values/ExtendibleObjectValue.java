package analysis.values;

/**
 * Object values that can have subclasses
 */
public class ExtendibleObjectValue extends ObjectWithNotNullValue {
    @Override
    public ObjectWithNotNullValue copy() {
        return null;
    }
}
