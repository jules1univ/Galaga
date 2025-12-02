package engine.utils;

public final class Size {
    
    public float width;
    public float height;

    public static Size of(float width, float height) {
        return new Size(width, height);
    }

    public static Size of(float size) {
        return new Size(size, size);
    }

    public static Size of(Size original, float scale) {
        return new Size(original.width * scale, original.height * scale);
    }

    public static Size half(Size original) {
        return new Size(original.width / 2, original.height / 2);
    }

    private Size(float width, float height) {
        this.width = width;
        this.height = height;
    }


    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public int getIntWidth() {
        return (int) this.width;
    }

    public int getIntHeight() {
        return (int) this.height;
    }
}
