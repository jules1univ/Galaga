package game.entities.ui;

import java.awt.Color;

import engine.ui.UIElement;
import game.Config;
import game.Galaga;

public class Menu extends UIElement {

    private boolean visible = true;

    public Menu() {
        super();
        this.visible = true;
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
        Galaga.getContext().getRenderer()
        .setFont(Config.TITLE_FONT_ALIAS)
        .drawText("< select >",(int) this.width/2,(int) this.height/2, Color.WHITE)
        .setFont(Config.DEFAULT_FONT_ALIAS);
    }
    
}
