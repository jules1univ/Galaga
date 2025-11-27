package engine.entity;

import engine.Application;

public abstract class Entity {
    protected float x, y;
    protected float width, height;

    public Entity() {
    }

    public final boolean collideWith(float x, float y, float width, float height) {
        return this.x < x + width &&
                this.x + this.width > x &&
                this.y < y + height &&
                this.y + this.height > y;
    }

    public final boolean collideWith(Entity e) {
        return this.collideWith(e.x, e.y, e.width, e.height);
    }

    public final boolean isOutOfBounds() {
        return !this.collideWith(0, 0,
                Application.getContext().getFrame().getWidth(),
                Application.getContext().getFrame().getHeight());
    }

    public final float getOffsetX() {
        return this.x;
    }

    public final float getOffsetY() {
        return this.y;
    }

    public final float getWidth() {
        return this.width;
    }

    public final float getHeight() {
        return this.height;
    }

    public abstract boolean init();

    public abstract void update(double dt);

    public abstract void draw();
}
