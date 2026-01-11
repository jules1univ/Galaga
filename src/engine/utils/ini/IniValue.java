package engine.utils.ini;

import java.util.Optional;

public final class IniValue {

    private final String value;

    public static IniValue of(String value) {
        return new IniValue(value);
    }

    private IniValue(String value) {
        this.value = value != null ? value : "";
    }

    public Optional<Integer> asInt() {
        try {
            return Optional.of(Integer.valueOf(this.value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }        
    }

    public Optional<Float> asFloat() {
        try {
            return Optional.of(Float.valueOf(this.value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }        
    }

    public Optional<Boolean> asBoolean() {
        return switch (this.value.toLowerCase()) {
            case "true", "1", "yes" -> Optional.of(true);
            case "false", "0", "no" -> Optional.of(false);
            default -> Optional.empty();
        };
    }

    @Override
    public String toString() {
        return this.value;
    }
}
