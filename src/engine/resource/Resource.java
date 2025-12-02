package engine.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;

import engine.utils.cache.Cache;
import engine.utils.logger.Log;

public abstract class Resource<ResourceData> {
    protected final ResourceAlias alias;
    protected ResourceData data;

    protected boolean loaded;

    public Resource(ResourceAlias alias) {
        this.alias = alias;
        this.data = null;
        this.loaded = false;
    }

    protected final InputStream getResourceData() {
        if(Cache.exists(this.alias.getName()))
        {
            Log.message("Resource '"+ this.alias.getName() + "' found in cache.");
            return Cache.load(this.alias.getName());
        }

        File file = this.alias.getPath();
        if (file.exists()) {
            try {
                Log.message("Resource '"+ this.alias.getName() + "' found : " + file);
                return new FileInputStream(file);
            } catch (Exception e) {
                Log.error("Resource file loading failed: " + e.getMessage());
            }
        }

        URI url = this.alias.getUrl();
        try{
            InputStream in = url.toURL().openStream();
            Cache.save(this.alias.getName(), in);
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
