package engine.elements;

import engine.utils.Position;
import engine.utils.Size;

public abstract class VisualElement {
    protected Position position;
    protected Size size;

    public Position getCenter() {
        return Position.ofCenter(this.position, this.size);
    }

    public final Position getPosition() {
        return this.position;
    }

    public final Size getSize() {
        return this.size;
    }

    public abstract boolean init();

    public abstract void update(double dt);

    public abstract void draw();
}
