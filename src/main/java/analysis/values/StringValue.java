package analysis.values;

public class StringValue extends PossibleValues {

    public StringValue() {
        // TODO: add domain for String
    }

    @Override
    public PossibleValues merge(PossibleValues other) {
        return other.mergeTo(this);
    }

    @Override
    protected PossibleValues mergeTo(StringValue other) {
        // TODO: implement merge
        return new StringValue();
    }

    @Override
    public PossibleValues add(PossibleValues other) {
        return other.addTo(this);
    }

    @Override
    protected StringValue addTo(PossibleValues target) {
        // TODO: implement add
        return new StringValue();
    }

    @Override
    protected StringValue addTo(IntegerRange target) {
        // TODO: implement add
        return new StringValue();
    }

    @Override
    protected StringValue addTo(StringValue target) {
        // TODO: implement add
        return new StringValue();
    }
}
