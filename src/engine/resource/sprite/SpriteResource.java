package engine.resource.sprite;

import engine.graphics.sprite.Sprite;
import engine.resource.Resource;
import engine.resource.ResourceAlias;
import engine.resource.ResourceCallback;
import java.io.InputStream;
import java.io.OutputStream;

public final class SpriteResource extends Resource<Sprite> {
    public static final String NAME = "sprite";
    
    public SpriteResource(ResourceAlias alias, ResourceCallback callback) {
        super(alias, callback);
    }

    @Override
    public Sprite read(InputStream in) {
        return Sprite.createSprite(in);
    }

    @Override
    public boolean write(Sprite data) {
        OutputStream out = this.getResourceOutput();
        if(out == null) {
            return false;
        }
        return Sprite.saveSprite(data, out);
    }
}
