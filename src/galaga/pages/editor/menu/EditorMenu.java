package galaga.pages.editor.menu;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import engine.elements.ui.Alignment;
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

public class EditorMenu extends Page<GalagaPage> {

    private TextSelectEnum<EditorMenuModeOption> etidorMode;
    private Text settings;
    private Text back;

    private Font titleFont;
    private Sky sky;

    private Sprite logo;
    private Position logoPosition;

    private EditorMenuOption option;

    private Sound themeSound;
    private Sound selectSound;

    public EditorMenu() {
        super(GalagaPage.EDITOR_MENU);
    }

    @Override
    public boolean onActivate() {

        this.size = Size.of(
                Galaga.getContext().getFrame().getWidth(),
                Galaga.getContext().getFrame().getHeight());

        this.titleFont = Galaga.getContext().getResource().get(Config.FONTS, Config.VARIANT_FONT_XLARGE);

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

        int padding = 50;

        this.logo = Galaga.getContext().getResource().get(Config.SPRITE_LOGO);
        this.logoPosition = Position.of(
                (this.size.getWidth()) / 2,
                (this.size.getHeight() - this.logo.getSize().getHeight() * Config.SPRITE_SCALE_ICON) / 2 - padding
                        - padding / 2);

        padding -= 10;
        this.etidorMode = new TextSelectEnum<>(
                EditorMenuModeOption.class,
                0,
                true,
                Color.WHITE, this.titleFont);
        if (!this.etidorMode.init()) {
            return false;
        }

        this.etidorMode.setPosition(Position.of(
                this.size.getWidth() / 2,
                this.size.getHeight() / 2));

        this.settings = new Text(
                "SETTINGS",
                Position.of(
                        this.size.getWidth() / 2,
                        this.etidorMode.getPosition().getY() + this.etidorMode.getSize().getHeight() / 2 + padding
                                + padding / 4.f),
                Color.WHITE, this.titleFont);
        if (!this.settings.init()) {
            return false;
        }
        this.settings.setCenter(Alignment.CENTER, Alignment.END);

        this.back = new Text(
                "BACK",
                Position.of(
                        this.size.getWidth() / 2,
                        this.settings.getPosition().getY() + this.settings.getSize().getHeight() / 2 + padding),
                Color.WHITE, this.titleFont);
        if (!this.back.init()) {
            return false;
        }
        this.back.setCenter(Alignment.CENTER, Alignment.END);

        this.option = EditorMenuOption.EDITORS;
        this.updateMenuSelect();

        return true;
    }

    @Override
    public boolean onDeactivate() {
        this.themeSound.stop();
        this.state = PageState.INACTIVE;
        return true;
    }

    @Override
    public void onReceiveArgs(Object... args) {
    }

    @Override
    public void update(float dt) {
        this.sky.update(dt);

        if (Galaga.getContext().getInput().isKeyPressed(
            Galaga.getContext().getState().keyboard.getKey("menu_navigate_left").orElse(KeyEvent.VK_LEFT)
        )) {
            this.selectSound.play(2.f);
            if (this.option == EditorMenuOption.EDITORS) {
                this.etidorMode.prev();
            }
            this.selectSound.play(2.f);
        } else if (Galaga.getContext().getInput().isKeyPressed(
            Galaga.getContext().getState().keyboard.getKey("menu_navigate_left").orElse(KeyEvent.VK_RIGHT)
        )) {
            this.selectSound.play(2.f);
            if (this.option == EditorMenuOption.EDITORS) {
                this.etidorMode.next();
            }
        } else if (Galaga.getContext().getInput().isKeyPressed(
            Galaga.getContext().getState().keyboard.getKey("menu_navigate_up").orElse(KeyEvent.VK_UP)
        )) {
            this.selectSound.play(2.f);
            switch (this.option) {
                case EDITORS -> {
                    this.option = EditorMenuOption.BACK;
                    this.updateMenuSelect();
                }
                case SETTINGS -> {
                    this.option = EditorMenuOption.EDITORS;
                    this.updateMenuSelect();
                }
                case BACK -> {
                    this.option = EditorMenuOption.SETTINGS;
                    this.updateMenuSelect();
                }
            }
        } else if (Galaga.getContext().getInput().isKeyPressed(
            Galaga.getContext().getState().keyboard.getKeys("menu_navigate_down","menu_navigate").orElse(List.of(KeyEvent.VK_DOWN, KeyEvent.VK_TAB))
        )) {
            this.selectSound.play(2.f);
            switch (this.option) {
                case EDITORS -> {
                    this.option = EditorMenuOption.SETTINGS;

                }
                case SETTINGS -> {
                    this.option = EditorMenuOption.BACK;
                }
                case BACK -> {
                    this.option = EditorMenuOption.EDITORS;
                }
            }
            this.updateMenuSelect();
        } else if (Galaga.getContext().getInput().isKeyPressed(
            Galaga.getContext().getState().keyboard.getKey("menu_confirm").orElse(KeyEvent.VK_ENTER)
        )) {
            this.selectSound.play(2.f);
            switch (this.option) {
                case EDITORS -> {
                    switch (this.etidorMode.getSelectedOption()) {
                        case CREATE_LEVEL ->
                            Galaga.getContext().getApplication().setCurrentPage(GalagaPage.EDITOR_LEVEL);
                        case CREATE_SPRITE ->
                            Galaga.getContext().getApplication().setCurrentPage(GalagaPage.EDITOR_SPRITE);
                        case CREATE_ENEMY ->
                            Galaga.getContext().getApplication().setCurrentPage(GalagaPage.EDITOR_ENEMY);
                    }
                }
                case SETTINGS -> Galaga.getContext().getApplication().setCurrentPage(GalagaPage.EDITOR_SETTINGS);
                case BACK -> Galaga.getContext().getApplication().setCurrentPage(GalagaPage.MAIN_MENU);
            }
        }
    }

    private void updateMenuSelect() {
        switch (this.option) {
            case EDITORS -> {
                this.etidorMode.setShowArrows(true);
                this.etidorMode.setColor(Color.ORANGE);
                this.settings.setColor(Color.WHITE);
                this.back.setColor(Color.WHITE);
            }
            case SETTINGS -> {
                this.etidorMode.setShowArrows(false);
                this.etidorMode.setColor(Color.WHITE);
                this.settings.setColor(Color.ORANGE);
                this.back.setColor(Color.WHITE);
            }
            case BACK -> {
                this.etidorMode.setShowArrows(false);
                this.etidorMode.setColor(Color.WHITE);
                this.settings.setColor(Color.WHITE);
                this.back.setColor(Color.ORANGE);
            }
        }
    }

    @Override
    public void draw(Renderer renderer) {
        this.sky.draw(renderer);
        renderer.drawSprite(this.logo, this.logoPosition, Config.SPRITE_SCALE_ICON);

        this.etidorMode.draw(renderer);
        this.settings.draw(renderer);
        this.back.draw(renderer);
    }

}
