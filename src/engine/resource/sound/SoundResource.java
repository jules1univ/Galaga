package engine.resource.sound;

import java.io.InputStream;
import engine.resource.Resource;
import engine.resource.ResourceAlias;
import engine.resource.ResourceCallback;
import engine.resource.ResourceVariant;

public class SoundResource extends Resource<Sound> {
    public static final String NAME = "sound";
    public static final int DEFAULT_PRELOAD_COUNT = 8;

    public SoundResource(ResourceAlias alias, ResourceCallback callback) {
        super(alias, callback);
    }

    public boolean load() {
        InputStream in = this.getResourceData();
        if (in == null) {
            return false;
        }

        ResourceVariant variant = this.alias.getVariant();
        int preloadCount = SoundResource.DEFAULT_PRELOAD_COUNT;
        if (variant != null) {
            preloadCount = variant.getValue();
        }
        Sound sound = Sound.createSound(in, preloadCount);
        if (sound == null) {
            return false;
        }
        this.onLoadComplete(sound);
        return true;
    }

    @Override
    public boolean write(Sound data) {
        throw new UnsupportedOperationException("Sound.write should not be called.");
    }

}
