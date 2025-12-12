package galaga.pages.menu;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import engine.elements.ui.icon.Icon;
import engine.elements.ui.select.IconSelect;
import engine.elements.ui.select.TextSelect;
import engine.graphics.sprite.Sprite;
import engine.utils.Position;
import engine.utils.Size;
import galaga.Config;
import galaga.Galaga;
import galaga.pages.GalagaPage;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

public class Menu extends Page<GalagaPage> {

    private IconSelect shipSelect;
    private TextSelect gameMode;
    private Font titleFont;

    public Menu() {
        super(GalagaPage.MENU);
    }

    @Override
    public void update(float dt) {

        if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_LEFT)) {
            this.gameMode.prev();
        } else if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_RIGHT)) {
            this.gameMode.next();
        } else if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_ENTER, KeyEvent.VK_SPACE)) {
            Galaga.getContext().getApplication().setCurrentPage(GalagaPage.GAME);
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

    @Override
    public boolean onActivate() {
        this.size = Size.of(
                Galaga.getContext().getFrame().getWidth(),
                Galaga.getContext().getFrame().getHeight());

        this.titleFont = Galaga.getContext().getResource().get(Config.FONTS, Config.VARIANT_FONT_XLARGE);
        this.gameMode = new TextSelect(
                new String[] { "SOLO", "MULTIPLAYER" },
                0,
                true,
                Color.WHITE, this.titleFont);
        this.gameMode.setPosition(Position.of(
                this.size.getWidth() / 2,
                this.size.getHeight() / 2));

        if (!this.gameMode.init()) {
            return false;
        }

        Icon[] icons = new Icon[Config.SPRITES_SHIP.size()];
        for (int i =0; i< Config.SPRITES_SHIP.size() ; i++){
            Sprite ship = Galaga.getContext().getResource().get(Config.SPRITES_SHIP.get(i));
            icons[i] = new Icon(ship, Config.SPRITE_SCALE_ICON);
            icons[i].init();
        }
        
        

        this.shipSelect = new IconSelect(
            icons,
            0,
            true,
            Color.WHITE, this.titleFont
        );
        if (!this.shipSelect.init()) {
            return false;
        }

        
        this.shipSelect.setPosition(Position.of(
                this.size.getWidth() / 2,
                gameMode.getPosition().getIntY()-(gameMode.getPosition().getIntY()/2)));

        

        this.state = PageState.ACTIVE;
        return true;
    }

    @Override
    public boolean onDeactivate() {
        this.state = PageState.INACTIVE;
        return true;
    }

}
