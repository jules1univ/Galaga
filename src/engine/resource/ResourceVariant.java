package engine.resource;

public final class ResourceVariant {

    private final String name;
    private final Object value;

    public static ResourceVariant of(String name, Object value) {
        return new ResourceVariant(name, value);
    }


    private ResourceVariant(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public <T> T getValue() {
        return (T)this.value;
    }
}
