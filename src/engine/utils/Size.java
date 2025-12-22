package engine.utils;

import engine.network.NetBuffer;
import engine.network.NetObject;

public final class Size implements NetObject {

    public float width;
    public float height;

    public static Size zero() {
        return new Size(0.f, 0.f);
    }

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

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public Size copy() {
        return new Size(this.width, this.height);
    }

    @Override
    public String toString() {
        return "Size(" + this.width + ", " + this.height + ")";
    }

    @Override
    public int getId() {
        return 0; // TODO
    }

    @Override
    public void read(NetBuffer buff) {
        this.width = buff.readFloat().orElse(0.0f);
        this.height = buff.readFloat().orElse(0.0f);
    }

    @Override
    public void write(NetBuffer buff) {
        buff.writeFloat(this.width);
        buff.writeFloat(this.height);
    }
}
