package galaga.gscript.types.values;

public record FloatValue(float value) implements Value {

    @Override
    public Float getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return Float.toString(value);
    }   
}
