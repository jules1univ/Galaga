package galaga.gscript.types.values;

public record BooleanValue(boolean value) implements Value {
    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}