package engine.ui;

public abstract class UIElement {
    protected float x, y;
    protected float width, height;

    public UIElement() {
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
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
