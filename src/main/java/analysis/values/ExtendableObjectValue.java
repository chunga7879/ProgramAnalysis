package analysis.values;

/**
 * Object values that can have subclasses
 */
public class ExtendableObjectValue extends ObjectWithNotNullValue {
    public static final ExtendableObjectValue VALUE = new ExtendableObjectValue();

    public ExtendableObjectValue() {
        super();
    }

    public ExtendableObjectValue(boolean canBeNull) {
        super(canBeNull);
    }

    @Override
    public ObjectWithNotNullValue copy() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return 31 * getClass().hashCode();
    }
}
