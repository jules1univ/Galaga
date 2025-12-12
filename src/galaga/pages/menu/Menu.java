package galaga.pages.menu;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import engine.elements.ui.icon.Icon;
import engine.elements.ui.select.IconSelect;
import engine.elements.ui.select.TextSelect;
import engine.graphics.sprite.Sprite;
import engine.resource.sound.Sound;
import engine.utils.Position;
import engine.utils.Size;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaSound;
import galaga.entities.sky.Sky;
import galaga.pages.GalagaPage;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

public class Menu extends Page<GalagaPage> {

    private IconSelect shipSelect;
    private TextSelect gameMode;
    private Sky sky;
    private Sprite logo;
    private Position logoPosition;

    private Sound themeSound;
    private Sound selectSound;

    private int indexSelect;

    private Font titleFont;

    public Menu() {
        super(GalagaPage.MENU);
    }

    @Override
    public void update(float dt) {
        this.sky.update(dt);

        if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_LEFT)) {
            if (this.indexSelect == 0) {
                this.gameMode.prev();
            } else {
                this.shipSelect.prev();
            }
            this.selectSound.play(2.f);
        } else if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_RIGHT)) {
            if (this.indexSelect == 0) {
                this.gameMode.next();
            } else {
                this.shipSelect.next();
            }
            this.selectSound.play(2.f);
        } else if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_UP, KeyEvent.VK_DOWN)) {
            this.indexSelect = (this.indexSelect + 1) % 2;
            this.gameMode.toogleArrows();
            this.shipSelect.toogleArrows();
            this.selectSound.play(2.f);
        } else if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_ENTER, KeyEvent.VK_SPACE)) {
            Galaga.getContext().getState().shipSkin = this.shipSelect.getSelected().getSprite();
            Galaga.getContext().getApplication().setCurrentPage(GalagaPage.GAME);
            this.selectSound.play(2.f);
        }
    }

    @Override
    public void draw() {
        this.sky.draw();

        Galaga.getContext().getRenderer().drawSprite(this.logo, this.logoPosition, Config.SPRITE_SCALE_ICON);
        this.gameMode.draw();
        this.shipSelect.draw();
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

        this.themeSound = Galaga.getContext().getResource().get(GalagaSound.name_entry_2nd5th);
        if (this.themeSound == null) {
            return false;
        }
        this.themeSound.play(0.2f);

        this.selectSound = Galaga.getContext().getResource().get(GalagaSound.menu_select);
        if (this.selectSound == null) {
            return false;
        }

        this.sky = new Sky(Config.SIZE_SKY_GRID);
        if (!this.sky.init()) {
            return false;
        }

        this.titleFont = Galaga.getContext().getResource().get(Config.FONTS, Config.VARIANT_FONT_XLARGE);

        int margin = 50;

        this.logo = Galaga.getContext().getResource().get(Config.SPRITE_LOGO);
        this.logoPosition = Position.of(
                (this.size.getWidth()) / 2,
                (this.size.getHeight() - this.logo.getSize().getHeight() * Config.SPRITE_SCALE_ICON) / 2 - margin
                        - margin / 2);

        this.gameMode = new TextSelect(
                new String[] { "SOLO", "MULTIPLAYER" },
                0,
                true,
                Color.WHITE, this.titleFont);
        if (!this.gameMode.init()) {
            return false;
        }
        this.gameMode.setPosition(Position.of(
                this.size.getWidth() / 2,
                this.size.getHeight() / 2));

        Icon[] icons = new Icon[Config.SPRITES_SHIP.size()];
        for (int i = 0; i < Config.SPRITES_SHIP.size(); i++) {
            Sprite ship = Galaga.getContext().getResource().get(Config.SPRITES_SHIP.get(i));
            icons[i] = new Icon(ship, Config.SPRITE_SCALE_MENU_ICON);
        }

        this.shipSelect = new IconSelect(
                icons,
                0,
                true,
                Color.WHITE, this.titleFont);
        if (!this.shipSelect.init()) {
            return false;
        }

        this.shipSelect.setPosition(Position.of(
                this.size.getWidth() / 2,
                this.gameMode.getPosition().getY() + this.gameMode.getSize().getHeight() + margin));
        this.shipSelect.toogleArrows();

        this.state = PageState.ACTIVE;
        return true;
    }

    @Override
    public boolean onDeactivate() {
        this.themeSound.stop();
        this.state = PageState.INACTIVE;
        return true;
    }

}
