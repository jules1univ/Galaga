package game.entities.ui;

import engine.elements.ui.UIElement;
import engine.utils.Size;
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
        this.size = Size.of(
            Galaga.getContext().getFrame().getWidth(),
            Galaga.getContext().getFrame().getHeight()
        );
        return true;
    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void draw() {
        // TODO: menu select with left/right arrows, validate with enter
        // TODO: create a select element in engine ui
        //  ---------------
        //  < START >
        //    SHIPS 
        //    QUIT
    }

}
