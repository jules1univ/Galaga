package engine.resource;

import java.util.HashMap;
import java.util.List;

import engine.utils.logger.Log;

public final class ResourceManager {
    private volatile boolean loading = false;
    private volatile String status = "";
    private volatile float progress = 0.0f;

    private Thread loadingThread = null;

    private HashMap<String, Class<? extends Resource<?>>> loaders = new HashMap<>();
    private HashMap<String, Resource<?>> resources = new HashMap<>();

    public ResourceManager() {

    }

    public void register(String loadername, Class<? extends Resource<?>> resourceClass) {
        this.loaders.put(loadername, resourceClass);
    }

    public void add(ResourceAlias alias, String loadername, Runnable callback) {
        Class<? extends Resource<?>> resourceClass = this.loaders.get(loadername);
        if (resourceClass == null) {
            Log.error("Resource loader not found: " + loadername);
        }
        try {
            this.resources.put(alias.getName() + (alias.getVariant() != null ? alias.getVariant().getName() : ""),
                    resourceClass.getDeclaredConstructor(ResourceAlias.class, Runnable.class).newInstance(alias,
                            callback));
        } catch (Exception e) {
            Log.error("Resource creation failed: " + e.getMessage());
        }
    }

    public void add(List<ResourceAlias> aliases, String loadername, Runnable callback) {
        for (ResourceAlias alias : aliases) {
            this.add(alias, loadername, callback);
        }
    }

    public void add(ResourceAlias alias, String loadername) {
        this.add(alias, loadername, null);
    }

    public void add(List<ResourceAlias> aliases, String loadername) {
        this.add(aliases, loadername, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        Resource<T> res = (Resource<T>) this.resources.get(name);
        if (res != null && res.isLoaded()) {
            return res.data;
        }
        return null;
    }

    public <T> T get(ResourceAlias alias) {
        return this.get(alias.getName());
    }

    public <T, E extends Enum<E>> T get(E enumConst) {
        return this.get(enumConst.name().toLowerCase());
    }

    public <T> T get(List<ResourceAlias> alias, String variant) {
        return this.get(alias.getFirst().getName() + variant);
    }

    public <T, E extends Enum<E>> T get(E enumConst, String variant) {
        return this.get(enumConst.name().toLowerCase() + variant);
    }

    public void load(Runnable callback, long delay) {
        this.loading = true;
        this.loadingThread = new Thread(() -> {
            int loadedCount = 0;
            for (Resource<?> res : this.resources.values()) {
                if (res.isLoaded()) {
                    continue;
                }

                loadedCount++;
                this.status = res.getAlias().getName();
                this.progress = (float) loadedCount / (float) this.resources.size();

                if (!res.load()) {
                    Log.error("Failed to load resource: " + res.getAlias().getName());
                    this.resources.remove(res.getAlias().getName());
                }

                if (Thread.currentThread().isInterrupted()) {
                    Log.message("Resource loading cancelled.");
                    break;
                }

                if (delay > 0) {
                    try {
                        Thread.sleep(delay);
                    } catch (Exception e) {
                    }
                }
            }
            this.loading = false;
            Log.message("Resources loaded successfully.");
            callback.run();
        });
        this.loadingThread.start();

    }

    public void cancel() {
        if (this.loading && this.loadingThread != null) {
            this.loadingThread.interrupt();
            this.loading = false;
        }
    }

    public String getStatus() {
        return this.status;
    }

    public float getProgress() {
        return this.progress;
    }

    public boolean isLoading() {
        return this.loading;
    }

}
