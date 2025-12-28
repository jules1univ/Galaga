package engine.elements.entity;

import engine.Application;
import engine.elements.VisualElement;
import engine.graphics.Renderer;
import engine.utils.Collision;
import java.awt.Color;

public abstract class Entity extends VisualElement {

    public Entity() {
        super();
    }

    public boolean collideWith(Entity entity) {
        return Collision.aabb(
                this.position,
                this.size,
                entity.getPosition(),
                entity.getSize());
    }

    @Override
    public void draw(Renderer renderer) {
        if (Application.DEBUG_MODE) {
            renderer.drawRectOutline(this.position, this.getSize(), Color.WHITE);
        }
    }

}
