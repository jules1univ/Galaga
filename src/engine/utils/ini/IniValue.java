package engine.utils.ini;

import java.util.Optional;

public final class IniValue {

    private final String value;

    public static IniValue of(String value) {
        return new IniValue(value);
    }

    private IniValue(String value) {
        this.value = value;
    }

    public Optional<Integer> asInt() {
        try {
            return Optional.of(Integer.parseInt(this.value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }        
    }

    public Optional<Float> asFloat() {
        try {
            return Optional.of(Float.parseFloat(this.value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }        
    }

    public Optional<Boolean> asBoolean() {
        String valLower = this.value.toLowerCase();
        if (valLower.equals("true") || valLower.equals("1") || valLower.equals("yes")) {
            return Optional.of(true);
        } else if (valLower.equals("false") || valLower.equals("0") || valLower.equals("no")) {
            return Optional.of(false);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return this.value;
    }
}
