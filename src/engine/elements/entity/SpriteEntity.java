package engine.elements.entity;

import engine.Application;
import engine.graphics.sprite.Sprite;
import engine.utils.Position;
import engine.utils.Size;

public abstract class SpriteEntity extends Entity {
    protected Sprite sprite;
    protected float angle;
    protected float scale;

    public SpriteEntity() {
        super();
    }

    public final Sprite getSprite() {
        return this.sprite;
    }

    public final float getAngle() {
        return this.angle;
    }

    public final float getScale() {
        return this.scale;
    }

    public final Size getScaledSize() {
        return Size.of(this.size, this.scale);
    }
    

    @Override
    public Position getCenter() {
        return Position.ofCenter(this.position, this.getScaledSize());
    }

    @Override
    public void draw() {
        Application.getContext().getRenderer().drawSprite(this);
    }

}
