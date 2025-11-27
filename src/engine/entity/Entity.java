package engine.entity;

import java.util.concurrent.atomic.AtomicInteger;

import engine.AppContext;

public abstract class Entity<Type extends Enum<Type>> {
    protected AppContext ctx;
    protected float x, y;
    protected float width, height;
    protected Type type;

    private int id;
    private static final AtomicInteger next = new AtomicInteger(1);

    public Entity(AppContext ctx) {
        this.ctx = ctx;
        this.id = next.getAndIncrement();
    }

    public final Type getType() {
        return this.type;
    }

    public final int getId() {
        return this.id;
    }

    public final boolean collideWith(float x, float y, float width, float height) {
        return this.x < x + width &&
                this.x + this.width > x &&
                this.y < y + height &&
                this.y + this.height > y;
    }

    public final boolean collideWith(Entity<?> e) {
        return this.collideWith(e.x, e.y, e.width, e.height);
    }

    public final boolean isOutOfBounds() {
        return !this.collideWith(0, 0, this.ctx.frame.getWidth(), this.ctx.frame.getHeight());
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
