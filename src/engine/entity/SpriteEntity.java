package engine.entity;

import engine.Application;
import engine.graphics.sprite.Sprite;
import engine.graphics.sprite.SpriteManager;

public abstract class SpriteEntity extends Entity {
    protected Sprite sprite;
    protected float angle;

    public SpriteEntity() {
        super();
    }

    public final Sprite getSprite() {
        return this.sprite;
    }

    public final float getAngle() {
        return this.angle;
    }

    protected final Sprite loadFromSprite(String name, String path, float scale) {
        if (!SpriteManager.getInstance().load(name, path, scale)) {
            return null;
        }

        Sprite sprite = SpriteManager.getInstance().get(name);
        this.width = sprite.getWidth();
        this.height = sprite.getHeight();
        return sprite;
    }

    @Override
    public void draw() {
        Application.getContext().getRenderer().drawSpriteEntity(this, true);
    }

}
