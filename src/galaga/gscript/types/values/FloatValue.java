package galaga.gscript.types.values;

public record FloatValue(float value) implements Value {
    @Override
    public String toString() {
        return Float.toString(value);
    }   
}
