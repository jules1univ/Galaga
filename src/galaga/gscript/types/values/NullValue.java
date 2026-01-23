package galaga.gscript.types.values;

public record NullValue() implements Value {

    @SuppressWarnings("unchecked")
    @Override
    public Void getValue() {
        return null;
    }

    @Override
    public String toString() {
        return "(null)";
    }

}
