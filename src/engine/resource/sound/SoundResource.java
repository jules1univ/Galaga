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
    public Sound read(InputStream in) {
        return Sound.createSound(in);
    }

    @Override
    public boolean write(Sound data) {
        throw new UnsupportedOperationException("Sound.write should not be called.");
    }

}
