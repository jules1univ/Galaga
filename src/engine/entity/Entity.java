package engine.entity;

import java.util.concurrent.atomic.AtomicInteger;

import engine.AppContext;
import engine.graphics.GraphicsEntity;
import engine.graphics.sprite.Sprite;
import engine.graphics.sprite.SpriteManager;

public abstract class Entity<Type extends Enum<Type>> implements GraphicsEntity {
    protected AppContext ctx;
    protected double x, y;
    protected double width, height;
    protected Type type;

    private int id;
    private static final AtomicInteger next = new AtomicInteger(1);

    public Entity(AppContext ctx) {
        this.ctx = ctx;
        this.id = next.getAndIncrement();
    }

    public Type getType() {
        return this.type;
    }

    public int getId() {
        return this.id;
    }

    public boolean collideWith(double x, double y, double width, double height) {
        return this.x < x + width &&
                this.x + this.width > x &&
                this.y < y + height &&
                this.y + this.height > y;
    }

    public boolean collideWith(Entity<?> e) {
        return this.collideWith(e.x, e.y, e.width, e.height);
    }

    public boolean isOutOfBounds() {
        return !this.collideWith(0, 0, this.ctx.frame.getWidth(), this.ctx.frame.getHeight());
    }

    public double getOffsetX() {
        return this.x;
    }

    public double getOffsetY() {
        return this.y;
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }

    protected Sprite loadFromSprite(String name, String path, float scale) {
        if (!SpriteManager.getInstance().load(name, path, scale)) {
            return null;
        }

        Sprite sprite = SpriteManager.getInstance().get(name);
        this.width = sprite.getWidth();
        this.height = sprite.getHeight();
        return sprite;
    }

    public abstract boolean init();

    public abstract void update(double dt);

    public abstract void draw();
}
