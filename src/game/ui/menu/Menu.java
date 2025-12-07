package game.ui.menu;

import engine.elements.ui.UIElement;
import engine.elements.ui.select.TextSelect;
import engine.utils.Position;
import engine.utils.Size;
import game.Config;
import game.Galaga;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

public class Menu extends UIElement {

    private boolean visible = true;
    private TextSelect gameMode;
    // private IconSelect shipSelect;

    private Font titleFont;

    public Menu() {
        super();
    }

    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public boolean init() {
        this.size = Size.of(
                Galaga.getContext().getFrame().getWidth(),
                Galaga.getContext().getFrame().getHeight());

        this.titleFont = Galaga.getContext().getResource().get(Config.FONTS, Config.VARIANT_FONT_XLARGE);
        this.gameMode = new TextSelect(
                new String[]{"SOLO", "MULTIPLAYER"},
                0,
                true,
                Color.WHITE, this.titleFont);
        this.gameMode.setPosition(Position.of(
                this.size.getWidth() / 2,
                this.size.getHeight() / 2));

        return this.gameMode.init();
    }

    @Override
    public void update(double dt) {

        if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_LEFT)) {
            this.gameMode.prev();
        } else if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_RIGHT)) {
            this.gameMode.next();
        } else if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_ENTER, KeyEvent.VK_SPACE)) {
            // TODO Select mode (Multiplayer/Solo)
            this.visible = false;
        }
    }

    @Override
    public void draw() {
        this.gameMode.draw();
        // TODO: menu select with left/right arrows, validate with enter
        // TODO: create a select element in engine ui
        // ---------------
        // < START >
        // SHIPS
        // QUIT
    }

}
