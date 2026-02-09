package engine.resource;

import engine.utils.logger.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.function.Function;

public abstract class Resource<ResourceData> {
    protected final ResourceAlias alias;
    protected ResourceData data;

    protected ResourceCallback callback;
    protected boolean loaded;

    public Resource(ResourceAlias alias, ResourceCallback callback) {
        this.alias = alias;
        this.data = null;
        this.loaded = false;
        this.callback = callback;
    }

    private void onLoadComplete(ResourceData d) {
        this.loaded = true;
        this.data = d;
        if (this.callback != null) {
            this.callback.run(this.alias.getVariant(), this);
        }
    }

    private boolean onLoadFallback() {
        Function<ResourceVariant, Object> fallback = this.alias.getFallback();
        if (fallback != null) {
            Object fallbackData = fallback.apply(this.alias.getVariant());
            if (fallbackData != null) {
                try {
                    ResourceData data = (ResourceData) fallbackData;
                    Log.warning("Resource '%s' loaded fallback data.", this.alias.getFullName());

                    this.onLoadComplete(data);
                    return true;
                } catch (ClassCastException e) {
                    Log.error("Resource '%s' fallback data type mismatch: %s",
                            this.alias.getFullName(), e.getMessage());
                }
            }
        }
        return false;
    }

    private InputStream getResourceInput() {
        File file = this.alias.getPath();
        if (file.exists()) {
            try {
                Log.message("Resource '%s' found in local file system.", this.alias.getFullName());
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                Log.error("Resource '%s' file loading failed: %s", this.alias.getFullName(), e.getMessage());
            }
        }

        URI url = this.alias.getUrl();
        if (url == null || url.toString().isEmpty()) {
            Log.error("Resource '%s' has no url to load from.", this.alias.getFullName());
            return null;
        }

        try (InputStream in = url.toURL().openStream()) {
            file.getParentFile().mkdirs();
            try (FileOutputStream out = new FileOutputStream(file)) {
                in.transferTo(out);
                Log.message("Resource '%s' saved to local file system.", this.alias.getFullName());
            } catch (IOException e) {
                Log.error("Resource '%s' saving failed: %s", this.alias.getFullName(), e.getMessage());
            }
            return new FileInputStream(file);
        } catch (IOException e) {
            Log.error("Resource '%s' download failed: %s", this.alias.getFullName(), e.getMessage());
        }

        return null;
    }

    protected OutputStream getResourceOutput() {
        File file = this.alias.getPath();
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            return new FileOutputStream(file);
        } catch (IOException e) {
            Log.error("Resource output stream creation failed: %s", e.getMessage());
            return null;
        }
    }

    public abstract boolean write(ResourceData data);

    public abstract ResourceData read(InputStream in);

    public final boolean load() {
        InputStream in = this.getResourceInput();
        if (in == null) {
            return this.onLoadFallback();
        }

        ResourceData data = this.read(in);
        if (data == null) {
            return this.onLoadFallback();
        }
        this.onLoadComplete(data);
        return true;
    }

    public final boolean isLoaded() {
        return this.loaded;
    }

    public final ResourceAlias getAlias() {
        return this.alias;
    }

    public final ResourceData getData() {
        return this.data;
    }
}
