package galaga.gscript.runtime.values;

public record IntegerValue(int value) implements Value {

    @Override
    public Integer getValue() {
        return this.value;
    }


    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
