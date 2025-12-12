package engine.graphics.sprite;

import engine.resource.Resource;
import engine.resource.ResourceAlias;
import engine.resource.ResourceCallback;
import java.io.InputStream;

public final class SpriteResource extends Resource<Sprite> {
    public static final String NAME = "sprite";
    
    public SpriteResource(ResourceAlias alias, ResourceCallback callback) {
        super(alias, callback);
    }

    @Override
    public boolean load() {
        InputStream in = this.getResourceData();
        if(in == null) {
            return false;
        }
        Sprite sprite = Sprite.createSprite(in);
        if(sprite == null) {
            return false;
        }
        this.onLoadComplete(sprite);
        return true;
    }

    @Override
    public boolean write(Sprite data) {
        throw new UnsupportedOperationException("Sprite.write should not be called");
    }
}
