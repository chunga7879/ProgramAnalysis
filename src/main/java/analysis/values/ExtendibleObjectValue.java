package analysis.values;

/**
 * Object values that can have subclasses
 */
public class ExtendibleObjectValue extends ObjectWithNotNullValue {
    @Override
    public ObjectWithNotNullValue copy() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        // TODO: implement
        return false;
    }

    @Override
    public int hashCode() {
        // TODO: implement
        return 0;
    }
}
