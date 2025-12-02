package game.entities.ui.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

import engine.elements.ui.UIElement;
import engine.elements.ui.select.TextSelect;
import engine.utils.Position;
import engine.utils.Size;
import game.Config;
import game.Galaga;

public class Menu extends UIElement {

    private boolean visible;

    private TextSelect gameMode;
    // private IconSelect shipSelect;

    private Font titleFont;

    public Menu() {
        super();
        this.visible = true;
        this.gameMode = new TextSelect(
                new String[] { "SOLO", "MULTIPLAYER" },
                0,
                true,
                Config.SIZE_FONT_XLARGE,
                Color.WHITE);
    }

    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public boolean init() {
        this.size = Size.of(
                Galaga.getContext().getFrame().getWidth(),
                Galaga.getContext().getFrame().getHeight());
                
        this.titleFont = Galaga.getContext().getResource().get(Config.DEFAULT_FONT, Config.VARIANT_FONT_XLARGE);
        Galaga.getContext().getRenderer().setFont(titleFont);
        this.gameMode.setPosition(Position.of(
                this.size.getWidth() / 2,
                this.size.getHeight() / 2));
        if (!this.gameMode.init()) {
            return false;
        }

        return true;
    }

    @Override
    public void update(double dt) {

        if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_LEFT)) {
            this.gameMode.prev();
        } else if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_RIGHT)) {
            this.gameMode.next();
        }
    }

    @Override
    public void draw() {
        if (!Galaga.getContext().getRenderer().isFont(titleFont)) {
            Galaga.getContext().getRenderer().setFont(titleFont);
        }
        this.gameMode.draw();
        // TODO: menu select with left/right arrows, validate with enter
        // TODO: create a select element in engine ui
        // ---------------
        // < START >
        // SHIPS
        // QUIT
    }

}
