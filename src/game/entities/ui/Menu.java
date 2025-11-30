package game.entities.ui;

import engine.ui.UIElement;
import game.Galaga;

public class Menu extends UIElement {

    private boolean visible;

    public Menu() {
        super();
        this.visible = false;
    }

    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public boolean init() {
        this.width = Galaga.getContext().getFrame().getWidth();
        this.height = Galaga.getContext().getFrame().getHeight();
        return true;
    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void draw() {

    }

}
