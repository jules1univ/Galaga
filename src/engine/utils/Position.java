package engine.utils;

public final class Position {
    private float x, y;

    public static Position zero() {
        return new Position(.0f, .0f);
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
                center.getY() - size.getHeight() / 2);
    }

    private Position(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Position add(float value) {
        this.x += value;
        this.y += value;
        return this;
    }

    public Position add(Position other) {
        this.x += other.x;
        this.y += other.y;
        return this;

    }

    public Position addX(float dx) {
        this.x += dx;
        return this;

    }

    public Position addY(float dy) {
        this.y += dy;
        return this;

    }

    public Position multiply(float factor) {
        this.x *= factor;
        this.y *= factor;
        return this;
    }

    public Position clampX(float minX, float maxX) {
        this.x = Math.max(minX, Math.min(this.x, maxX));
        return this;

    }

    public Position clampY(float minY, float maxY) {
        this.y = Math.max(minY, Math.min(this.y, maxY));
        return this;
    }

    public Position setX(float x) {
        this.x = x;
        return this;

    }

    public Position setY(float y) {
        this.y = y;
        return this;

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

    public float distance(Position other) {
        float dx = this.x - other.x;
        float dy = this.y - other.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public float angleTo(Position other) {
        return (float) Math.toDegrees((float) Math.atan2(this.y - other.y, this.x - other.x));
    }

    public Position normalize() {
        float length = (float) Math.sqrt(this.x * this.x + this.y * this.y);
        if (length == 0) {
            return this;
        }
        this.x /= length;
        this.y /= length;
        return this;
    }

    public Position half() {
        this.x /= 2;
        this.y /= 2;
        return this;
    }

    public Position negate() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }

    public Position abs() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        return this;
    }

    public Position moveTo(float angle, float move) {
        float rad = (float) Math.toRadians(angle);
        this.x += Math.cos(rad) * move;
        this.y += Math.sin(rad) * move;
        return this;
    }

    public Position moveTo(Position target, float move) {
        float dx = target.x - this.x;
        float dy = target.y - this.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        if (distance <= move || distance == 0) {
            this.x = target.x;
            this.y = target.y;
        } else {
            this.x += (dx / distance) * move;
            this.y += (dy / distance) * move;
        }
        return this;
    }

   
    public Position copy() {
        return new Position(this.x, this.y);
    }

    @Override
    public String toString() {
        return "Position(" + this.x + ", " + this.y + ")";
    }
}
