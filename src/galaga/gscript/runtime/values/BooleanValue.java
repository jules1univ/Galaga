package galaga.gscript.runtime.values;

public record BooleanValue(boolean value) implements Value {

    @Override
    public Boolean getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}