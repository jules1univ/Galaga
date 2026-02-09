package galaga.pages.menu;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import engine.elements.ui.Alignment;
import engine.elements.ui.icon.Icon;
import engine.elements.ui.select.IconSelect;
import engine.elements.ui.select.TextSelectEnum;
import engine.elements.ui.text.Text;
import engine.graphics.Renderer;
import engine.graphics.sprite.Sprite;
import engine.resource.sound.Sound;
import engine.utils.Position;
import engine.utils.Size;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaPage;
import galaga.entities.sky.Sky;
import galaga.resources.sound.GalagaSound;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.List;

public class MainMenu extends Page<GalagaPage> {

    private IconSelect shipSelect;
    private TextSelectEnum<MainMenuModeOption> gameMode;
    private Text quit;

    private Sky sky;
    private Sprite logo;
    private Position logoPosition;

    private Sound themeSound;
    private Sound selectSound;

    private MainMenuOption option;

    private Font titleFont;

    public MainMenu() {
        super(GalagaPage.MAIN_MENU);
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
    public boolean onActivate() {
        this.size = Size.of(
                Galaga.getContext().getFrame().getWidth(),
                Galaga.getContext().getFrame().getHeight());

        this.themeSound = Galaga.getContext().getResource().get(GalagaSound.menu_theme);
        if (this.themeSound == null) {
            return false;
        }
        this.themeSound.setLoop(true);
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

        int padding = 50;

        this.logo = Galaga.getContext().getResource().get(Config.SPRITE_LOGO);
        this.logoPosition = Position.of(
                (this.size.getWidth()) / 2,
                (this.size.getHeight() - this.logo.getSize().getHeight() * Config.SPRITE_SCALE_ICON) / 2 - padding
                        - padding / 2);

        this.gameMode = new TextSelectEnum<>(
                MainMenuModeOption.class,
                0,
                true,
                Color.WHITE, this.titleFont);
        if (!this.gameMode.init()) {
            return false;
        }
        this.gameMode.setPosition(Position.of(
                this.size.getWidth() / 2,
                this.size.getHeight() / 2));

        final int iconsSize = Config.SPRITES_SHIP.size() + Config.SPRITES_CUSTOM_SHIPS.size();
        Icon[] icons = new Icon[iconsSize];
        for (int i = 0; i < iconsSize; i++) {
            if (i < Config.SPRITES_SHIP.size()) {
                Sprite ship = Galaga.getContext().getResource().get(Config.SPRITES_SHIP.get(i));
                icons[i] = new Icon(ship, Config.SPRITE_SCALE_MENU_ICON);
                continue;
            }
            Sprite ship = Galaga.getContext().getResource()
                    .get(Config.SPRITES_CUSTOM_SHIPS.get(i - Config.SPRITES_SHIP.size()));
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
                this.gameMode.getPosition().getY() + this.gameMode.getSize().getHeight() + padding));
        this.shipSelect.setShowArrows(false);

        this.quit = new Text(
                "QUIT",
                Position.of(
                        this.size.getWidth() / 2,
                        this.shipSelect.getPosition().getY() + this.shipSelect.getSize().getHeight() + padding),
                Color.WHITE, this.titleFont);
        if (!this.quit.init()) {
            return false;
        }
        this.quit.setCenter(Alignment.CENTER, Alignment.END);

        this.option = MainMenuOption.GAMEMODE;
        this.updateMenuSelect();

        Galaga.getContext().getInput().resetPressedKeys();
        this.state = PageState.ACTIVE;
        return true;
    }

    @Override
    public boolean onDeactivate() {
        this.themeSound.stop();
        this.state = PageState.INACTIVE;
        return true;
    }

    @Override
    public void update(float dt) {
        this.sky.update(dt);

        if (Galaga.getContext().getInput().isKeyPressed(
            Galaga.getContext().getState().keyboard.getKey("menu_navigate_left").orElse(KeyEvent.VK_LEFT)
        )) {
            this.selectSound.play(2.f);
            switch (this.option) {
                case GAMEMODE -> this.gameMode.prev();
                case SHIPSKIN -> this.shipSelect.prev();
                default -> {
                }
            }
            this.selectSound.play(2.f);
        } else if (Galaga.getContext().getInput().isKeyPressed(
            Galaga.getContext().getState().keyboard.getKey("menu_navigate_right").orElse(KeyEvent.VK_RIGHT)
        )) {
            this.selectSound.play(2.f);
            switch (this.option) {
                case GAMEMODE -> this.gameMode.next();
                case SHIPSKIN -> this.shipSelect.next();
                default -> {
                }
            }
        } else if (Galaga.getContext().getInput().isKeyPressed(
            Galaga.getContext().getState().keyboard.getKey("menu_navigate_up").orElse(KeyEvent.VK_UP)
        )) {
            this.selectSound.play(2.f);
            switch (this.option) {
                case GAMEMODE -> {
                    this.option = MainMenuOption.QUIT;
                    this.updateMenuSelect();
                }
                case SHIPSKIN -> {
                    this.option = MainMenuOption.GAMEMODE;
                    this.updateMenuSelect();
                }
                case QUIT -> {
                    this.option = MainMenuOption.SHIPSKIN;
                    this.updateMenuSelect();
                }
            }
        } else if (Galaga.getContext().getInput().isKeyPressed(
            Galaga.getContext().getState().keyboard.getKeys("menu_navigate_down", "menu_navigate").orElse(List.of(KeyEvent.VK_DOWN, KeyEvent.VK_TAB))
        )) {
            this.selectSound.play(2.f);
            switch (this.option) {
                case GAMEMODE -> {
                    this.option = MainMenuOption.SHIPSKIN;
                    this.updateMenuSelect();

                }
                case SHIPSKIN -> {
                    this.option = MainMenuOption.QUIT;
                    this.updateMenuSelect();

                }
                case QUIT -> {
                    this.option = MainMenuOption.GAMEMODE;
                    this.updateMenuSelect();
                }
            }
        } else if (Galaga.getContext().getInput().isKeyPressed(
            Galaga.getContext().getState().keyboard.getKey("menu_confirm").orElse(KeyEvent.VK_ENTER)
        )) {
            this.selectSound.play(2.f);
            switch (this.option) {
                case QUIT -> Galaga.getContext().getApplication().stop();
                case SHIPSKIN -> {
                    Galaga.getContext().getState().shipSkin = this.shipSelect.getSelected().getSprite();
                }
                case GAMEMODE -> {
                    switch (this.gameMode.getSelectedOption()) {
                        case SOLO -> {
                            Galaga.getContext().getState().shipSkin = this.shipSelect.getSelected().getSprite();
                            Galaga.getContext().getApplication().setCurrentPage(GalagaPage.SOLO_GAME);
                        }
                        case MULTIPLAYER -> {
                            Galaga.getContext().getApplication().setCurrentPage(GalagaPage.MULTIPLAYER_MENU);
                        }
                        case EDITOR -> Galaga.getContext().getApplication().setCurrentPage(GalagaPage.EDITOR_MENU);
                    }
                }
            }
        }
    }

    @Override
    public void draw(Renderer renderer) {
        this.sky.draw(renderer);

        renderer.drawSprite(this.logo, this.logoPosition, Config.SPRITE_SCALE_ICON);

        this.gameMode.draw(renderer);
        this.shipSelect.draw(renderer);
        this.quit.draw(renderer);
    }

    @Override
    public void onReceiveArgs(Object... args) {
    }

}
