package engine.resource;

import engine.utils.cache.Cache;
import engine.utils.logger.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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
        if(Cache.exists(this.alias.getFullName()))
        {
            Log.message("Resource '"+ this.alias.getFullName() + "' found in cache.");
            return Cache.load(this.alias.getFullName());
        }

        File file = this.alias.getPath();
        if (file.exists()) {
            try {
                Log.message("Resource '"+ this.alias.getFullName() + "' found in local file system.");
                return new FileInputStream(file);
            } catch (Exception e) {
                Log.error("Resource '"+this.alias.getFullName() +"'file loading failed: " + e.getMessage());
            }
        }


        URI url = this.alias.getUrl();
        if(url.toString().isEmpty()) {
            Log.error("Resource '"+ this.alias.getFullName() + "' has no url to load from.");
            return null;
        }
        try{
            InputStream in = url.toURL().openStream();
            Cache.save(this.alias.getFullName(), in);
            return url.toURL().openStream();
        }catch(Exception e) {
            Log.error("Resource url loading failed: " + e.getMessage());
        }

        return null;
    }

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
