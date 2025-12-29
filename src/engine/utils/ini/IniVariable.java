package engine.utils.ini;

import java.util.Optional;

public final class IniVariable {

    private final String value;

    public IniVariable(String value) {
        this.value = value;
    }

    public String asString() {
        return value;
    }

    public Optional<Integer> asInt() {
        try {
            return Optional.of(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }        
    }

    public Optional<Float> asFloat() {
        try {
            return Optional.of(Float.parseFloat(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }        
    }

    public Optional<Boolean> asBoolean() {
        String valLower = value.toLowerCase();
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
        return value;
    }
}
