package engine.resource;

import engine.utils.logger.Log;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;

public final class ResourceManager {
    private volatile boolean loading = false;
    private volatile String status = "";
    private volatile float progress = .0f;

    private Thread loadingThread = null;

    private final LinkedHashMap<String, Class<? extends Resource<?>>> loaders = new LinkedHashMap<>();
    private final LinkedHashMap<String, Resource<?>> resources = new LinkedHashMap<>();

    public ResourceManager() {

    }

    public void register(String loadername, Class<? extends Resource<?>> resourceClass) {
        this.loaders.put(loadername, resourceClass);
    }

    public void add(ResourceAlias alias, String loadername, ResourceCallback callback) {
        Class<? extends Resource<?>> resourceClass = this.loaders.get(loadername);
        if (resourceClass == null) {
            Log.error("Resource loader not found: " + loadername);
            return;
        }
        
        try {
            this.resources.put(alias.getFullName(),
                    resourceClass.getDeclaredConstructor(ResourceAlias.class, ResourceCallback.class).newInstance(alias,
                            callback));
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException
                | SecurityException | InvocationTargetException e) {
            Log.error("Resource creation failed: " + e.getMessage());
        }
    }

    public void add(List<ResourceAlias> aliases, String loadername, ResourceCallback callback) {
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
        if(alias.isEmpty()) {
            return null;
        }
        return this.get(alias.get(0).getName() + variant);
    }

    public <T, E extends Enum<E>> T get(E enumConst, String variant) {
        return this.get(enumConst.name().toLowerCase() + variant);
    }

    public <T> boolean write(String name, T data) {
        @SuppressWarnings("unchecked")
        Resource<T> res = (Resource<T>)this.resources.get(name);
        if (res != null && res.isLoaded()) {
            return res.write(data);
        }
        return false;
    }

    public <T> boolean write(ResourceAlias alias, T data) {
        return this.write(alias.getName(), data);
    }

    public void load(Runnable callback, long delay) {
        this.loading = true;
        this.loadingThread = new Thread(() -> {
            int loadedCount = 0;
            int failedCount = 0;
            for (Resource<?> res : this.resources.values()) {
                if (res.isLoaded()) {
                    continue;
                }

                loadedCount++;
                this.status = res.getAlias().getName();
                this.progress = (float) loadedCount / (float) this.resources.size();

                if (!res.load()) {
                    failedCount++;
                    Log.error("Failed to load resource: " + res.getAlias().getFullName());
                }

                if (Thread.currentThread().isInterrupted()) {
                    Log.message("Resource loading cancelled.");
                    break;
                }

                if (delay > 0) {
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        Log.message("Resource loading cancelled.");
                        break;
                    }
                }
            }
            this.loading = false;
            if (failedCount > 0) {
                Log.error("Resource loading completed with " + failedCount + "  failed resources.");
            } else {
                Log.message("All resources loaded successfully.");
            }
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
