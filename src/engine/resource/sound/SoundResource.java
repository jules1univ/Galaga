package engine.resource.sound;

import java.io.InputStream;
import engine.resource.Resource;
import engine.resource.ResourceAlias;
import engine.resource.ResourceCallback;

public class SoundResource extends Resource<Sound> {
    public static final String NAME = "sound";

    public SoundResource(ResourceAlias alias, ResourceCallback callback) {
        super(alias, callback);
    }

     public boolean load() {
        InputStream in = this.getResourceData();
        if(in == null) {
            return false;
        }
        Sound sound = Sound.createSound(in);
        if(sound == null) {
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
