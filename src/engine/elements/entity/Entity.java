package engine.elements.entity;

import engine.Application;
import engine.elements.VisualElement;
import java.awt.Color;

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

    public boolean collideWith(Entity entity) {
        return this.collideWith(entity.getPosition().getY(), entity.getPosition().getY(), entity.getSize().getWidth(), entity.getSize().getHeight());
    }

    public final boolean isOutOfBounds() {
        return !this.collideWith(0, 0,
                Application.getContext().getFrame().getWidth(),
                Application.getContext().getFrame().getHeight());
    }

    @Override
    public void draw()
    {
        if(Application.DEBUG_MODE) {
            Application.getContext().getRenderer().drawRectOutline(this.position, this.getSize(), Color.WHITE);
        }
    }

}
