package galaga.pages.menu;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import engine.elements.ui.icon.Icon;
import engine.elements.ui.select.IconSelect;
import engine.elements.ui.select.TextSelect;
import engine.elements.ui.text.Text;
import engine.elements.ui.text.TextPosition;
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
    private Text quit;

    private Sky sky;
    private Sprite logo;
    private Position logoPosition;

    private Sound themeSound;
    private Sound selectSound;

    private MenuOption option;

    private Font titleFont;

    public Menu() {
        super(GalagaPage.MENU);
    }

    @Override
    public void update(float dt) {
        this.sky.update(dt);

        if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_LEFT)) {
            this.selectSound.play(2.f);
            switch (this.option) {
                case GAMEMODE -> this.gameMode.prev();
                case SHIPSKIN -> this.shipSelect.prev();
                default -> {
                }
            }
            this.selectSound.play(2.f);
        } else if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_RIGHT)) {
            this.selectSound.play(2.f);
            switch (this.option) {
                case GAMEMODE -> this.gameMode.next();
                case SHIPSKIN -> this.shipSelect.next();
                default -> {
                }
            }
        } else if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_UP)) {
            this.selectSound.play(2.f);
            switch (this.option) {
                case GAMEMODE -> {
                    this.option = MenuOption.QUIT;
                    this.updateMenuSelect();
                }
                case SHIPSKIN -> {
                    this.option = MenuOption.GAMEMODE;
                    this.updateMenuSelect();
                }
                case QUIT -> {
                    this.option = MenuOption.SHIPSKIN;
                    this.updateMenuSelect();
                }
            }
        } else if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_DOWN)) {
            this.selectSound.play(2.f);
            switch (this.option) {
                case GAMEMODE -> {
                    this.option = MenuOption.SHIPSKIN;
                    this.updateMenuSelect();

                }
                case SHIPSKIN -> {
                    this.option = MenuOption.QUIT;
                    this.updateMenuSelect();

                }
                case QUIT -> {
                    this.option = MenuOption.GAMEMODE;
                    this.updateMenuSelect();
                }
            }
        } else if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_ENTER, KeyEvent.VK_SPACE)) {
            this.selectSound.play(2.f);
            switch (this.option) {
                case QUIT -> Galaga.getContext().getApplication().stop();
                case GAMEMODE -> {
                    Galaga.getContext().getState().shipSkin = this.shipSelect.getSelected().getSprite();
                    Galaga.getContext().getApplication().setCurrentPage(GalagaPage.GAME);
                }
                default -> {
                }
            }
        }
    }

    private void updateMenuSelect() {
        switch (this.option) {
            case GAMEMODE -> {
                this.gameMode.setShowArrows(true);
                this.shipSelect.setShowArrows(false);

                this.gameMode.setColor(Color.ORANGE);
                this.shipSelect.setColor(Color.WHITE);
                this.quit.setColor(Color.WHITE);
            }
            case SHIPSKIN -> {
                this.gameMode.setShowArrows(false);
                this.shipSelect.setShowArrows(true);

                this.gameMode.setColor(Color.WHITE);
                this.shipSelect.setColor(Color.ORANGE);
                this.quit.setColor(Color.WHITE);
            }
            case QUIT -> {
                this.gameMode.setShowArrows(false);
                this.shipSelect.setShowArrows(false);

                this.gameMode.setColor(Color.WHITE);
                this.shipSelect.setColor(Color.WHITE);
                this.quit.setColor(Color.ORANGE);
            }
        }
    }

    @Override
    public void draw() {
        this.sky.draw();

        Galaga.getContext().getRenderer().drawSprite(this.logo, this.logoPosition, Config.SPRITE_SCALE_ICON);

        this.gameMode.draw();
        this.shipSelect.draw();
        this.quit.draw();
    }

    @Override
    public boolean onActivate() {
        this.size = Size.of(
                Galaga.getContext().getFrame().getWidth(),
                Galaga.getContext().getFrame().getHeight());

        Sound startSound = Galaga.getContext().getResource().get(GalagaSound.start_music);
        if (startSound != null) {
            startSound.stop();
        }

        this.themeSound = Galaga.getContext().getResource().get(GalagaSound.name_entry_2nd5th);
        if (this.themeSound == null) {
            return false;
        }
        this.themeSound.play(0.2f);

        this.selectSound = Galaga.getContext().getResource().get(GalagaSound.menu_select);
        if (this.selectSound == null) {
            return false;
        }
        this.selectSound.setCapacity(4);

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
        this.shipSelect.setShowArrows(false);

        this.quit = new Text(
                "QUIT",
                Position.of(
                        this.size.getWidth() / 2,
                        this.shipSelect.getPosition().getY() + this.shipSelect.getSize().getHeight() + margin),
                Color.WHITE, this.titleFont);
        if (!this.quit.init()) {
            return false;
        }
        this.quit.setCenter(TextPosition.CENTER, TextPosition.END);


        this.option = MenuOption.GAMEMODE;
        this.updateMenuSelect();
        
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
