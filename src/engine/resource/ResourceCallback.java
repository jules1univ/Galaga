package engine.resource;

@FunctionalInterface
public interface ResourceCallback {
    void run(ResourceVariant variant, Resource<?> resource);
}