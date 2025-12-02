package engine.elements.entity;

import engine.Application;
import engine.elements.VisualElement;

public abstract class Entity extends VisualElement {

    public Entity() {
        super();
    }

    public final boolean collideWith(float x, float y, float width, float height) {
        return this.position.getX() < x + width &&
                this.position.getX() + this.size.getWidth() > x &&
                this.position.getY() < y + height &&
                this.position.getY() + this.size.getHeight() > y;
    }

    public final boolean collideWith(Entity e) {
        return this.collideWith(e.getPosition().getY(), e.getPosition().getY(), e.getSize().getWidth(), e.getSize().getHeight());
    }

    public final boolean isOutOfBounds() {
        return !this.collideWith(0, 0,
                Application.getContext().getFrame().getWidth(),
                Application.getContext().getFrame().getHeight());
    }

}
