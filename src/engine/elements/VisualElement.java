package engine.elements;

import engine.graphics.Renderer;
import engine.utils.Position;
import engine.utils.Size;

public abstract class VisualElement {
    protected Position position;
    protected Size size;

    public Position getCenter() {
        return Position.ofCenter(this.position, this.size);
    }

    public final Position getPosition() {
        return this.position.copy();
    }

    public final Size getSize() {
        return this.size.copy();
    }

    public abstract boolean init();

    public abstract void update(float dt);

    public abstract void draw(Renderer renderer);
}
