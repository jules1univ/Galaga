package engine.graphics.sprite;

import java.io.InputStream;

import engine.resource.Resource;
import engine.resource.ResourceAlias;

public final class SpriteResource extends Resource<Sprite> {

    public SpriteResource(ResourceAlias alias) {
        super(alias);
    }

    public SpriteResource addScale(float scale) {
        return this;
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
        this.data = sprite;
        this.loaded = true;
        return true;
    }

}
