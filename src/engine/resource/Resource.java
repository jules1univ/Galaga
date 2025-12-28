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

    protected final void onLoadComplete(ResourceData d) {
        this.loaded = true;
        this.data = d;
        if (this.callback != null) {
            this.callback.run(this.alias.getVariant(), this);
        }
    }

    protected final InputStream getResourceData() {
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
        if (url.toString().isEmpty()) {
            Log.error("Resource '%s' has no url to load from.", this.alias.getFullName());
            return null;
        }
        try {
            InputStream in = url.toURL().openStream();
            new Thread(() -> {
                this.alias.getPath().getParentFile().mkdirs();
                try (FileOutputStream out = new FileOutputStream(this.alias.getPath())) {
                    in.transferTo(out);
                    Log.message("Resource '%s' saved to local file system.", this.alias.getFullName());
                } catch (Exception e) {
                    Log.error("Resource '%s' saving failed: %s", this.alias.getFullName(), e.getMessage());
                }
            }).start();
            return url.toURL().openStream();
        } catch (IOException e) {
            Log.error("Resource '%s' download failed: %s", this.alias.getFullName(), e.getMessage());
        }

        return null;
    }

    protected final OutputStream getResourceOutput() {
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

    public abstract boolean load();

    public boolean isLoaded() {
        return this.loaded;
    }

    public ResourceAlias getAlias() {
        return this.alias;
    }

    public ResourceData getData() {
        return this.data;
    }
}
