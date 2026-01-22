package galaga.gscript.types.values;

public record IntegerValue(int value) implements Value {
    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
