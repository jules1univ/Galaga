package engine.ui.icon;

import engine.Application;
import engine.graphics.sprite.Sprite;
import engine.ui.UIElement;

public final class Icon extends UIElement {

    private Sprite icon;

    private float scale;

    public Icon(Sprite icon, float scale) {
        super();
        this.icon = icon;
        this.scale = scale;
    }

    @Override
    public boolean init() {
        this.width = (int) (this.icon.getWidth() * this.scale);
        this.height = (int) (this.icon.getHeight() * this.scale);

        return true;
    }

    @Override
    public void update(double dt) {
    }

    @Override
    public void draw() {
        Application.getContext().getRenderer().drawSprite(this.icon, this.x, this.y, this.scale);
    }

}
