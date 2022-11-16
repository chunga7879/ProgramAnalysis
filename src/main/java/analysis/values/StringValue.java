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
    public PossibleValues mergeTo(StringValue other) {
        // TODO: implement merge
        return new StringValue();
    }

    @Override
    public PossibleValues add(PossibleValues other) {
        return other.addTo(this);
    }

    @Override
    public StringValue addTo(PossibleValues firstValue) {
        // TODO: implement add
        return new StringValue();
    }

    @Override
    public StringValue addTo(IntegerRange firstValue) {
        // TODO: implement add
        return new StringValue();
    }

    @Override
    public StringValue addTo(StringValue firstValue) {
        // TODO: implement add
        return new StringValue();
    }
}
