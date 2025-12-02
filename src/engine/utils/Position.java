package engine.utils;

public final class Position {
    private float x, y;

    public static Position zero() {
        return new Position(0.0f, 0.0f);
    }

    public static Position of(float x, float y) {
        return new Position(x, y);
    }

    public static Position of(Size size) {
        return new Position(size.getWidth(), size.getHeight());
    }

    public static Position ofCenter(Position center, Size size) {
        return Position.of(
                center.getX() - size.getWidth() / 2,
                center.getY() - size.getHeight() / 2
        );
    }
    

    private Position(float x, float y) {
        this.x = x;
        this.y = y;
    }


    public void addX(float dx) {
        this.x += dx;
    }

    public void addY(float dy) {
        this.y += dy;
    }

    public void clampX(float minX, float maxX) {
        this.x = Math.clamp(this.x, minX, maxX);
    }

    public void clampY(float minY, float maxY) {
        this.y = Math.clamp(this.y, minY, maxY);
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public int getIntX() {
        return (int) this.x;
    }

    public int getIntY() {
        return (int) this.y;
    }

    public Position copy() {
        return new Position(this.x, this.y);
    }
}
