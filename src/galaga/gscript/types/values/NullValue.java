package galaga.gscript.types.values;

public record NullValue() implements Value {
    @Override
    public String toString() {
        return "(null)";
    }
    
}
