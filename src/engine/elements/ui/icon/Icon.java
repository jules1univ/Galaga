package engine.elements.ui.icon;

import engine.Application;
import engine.elements.ui.UIElement;
import engine.graphics.sprite.Sprite;
import engine.utils.Size;

public final class Icon extends UIElement {

    private Sprite icon;

    private float scale;

    public Icon(Sprite icon, float scale) {
        super();
        this.icon = icon;
        this.scale = scale;
    }

    public Sprite getSprite() {
        return this.icon;
    }

    @Override
    public boolean init() {
        this.size = Size.of(this.icon.getSize(), this.scale);
        return true;
    }

    @Override
    public void update(float dt) {
    }

    @Override
    public void draw() {
        Application.getContext().getRenderer().drawSprite(this.icon, this.position, this.scale);
    }

}
