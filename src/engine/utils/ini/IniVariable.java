package engine.utils.ini;

public final class IniVariable {

    private final String value;

    public IniVariable(String value) {
        this.value = value;
    }

    public String asString() {
        return value;
    }

    public int asInt() {
        return Integer.parseInt(value);
    }

    public float asFloat() {
        return Float.parseFloat(value);
    }

    public boolean asBoolean() {
        return Boolean.parseBoolean(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
