package engine.elements.entity;

import engine.Application;
import engine.graphics.sprite.Sprite;
import engine.utils.Collision;
import engine.utils.Position;
import engine.utils.Size;
import java.awt.Color;

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
    public boolean collideWith(Entity entity) {
        if (entity instanceof SpriteEntity spriteEntity) {
            return Collision.aabb(
                    this.getCenter(),
                    this.getScaledSize(),
                    spriteEntity.getPosition(),
                    spriteEntity.getScaledSize());
        }

        return Collision.aabb(
                this.position,
                this.getScaledSize(),
                entity.getPosition(),
                entity.getSize());
    }

    @Override
    public void draw() {
        Application.getContext().getRenderer().drawSprite(this);
        if (Application.DEBUG_MODE) {
            Application.getContext().getRenderer().drawRectOutline(this.getCenter(), this.getScaledSize(), Color.WHITE);
        }
    }

}
