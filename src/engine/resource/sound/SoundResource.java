package engine.resource.sound;

import engine.resource.Resource;
import engine.resource.ResourceAlias;
import engine.resource.ResourceCallback;
import java.io.InputStream;
import java.io.OutputStream;

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
    protected boolean write(Sound data, OutputStream out) {
        return false;
    }

}
