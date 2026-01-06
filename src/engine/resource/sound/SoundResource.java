package engine.resource.sound;

import engine.resource.Resource;
import engine.resource.ResourceAlias;
import engine.resource.ResourceCallback;
import java.io.InputStream;

public final class SoundResource extends Resource<Sound> {
    public static final String NAME = "sound";

    public SoundResource(ResourceAlias alias, ResourceCallback callback) {
        super(alias, callback);
    }

    @Override
    public boolean load() {
        InputStream in = this.getResourceInput();
        if (in == null) {
            return false;
        }

        Sound sound = Sound.createSound(in);
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
