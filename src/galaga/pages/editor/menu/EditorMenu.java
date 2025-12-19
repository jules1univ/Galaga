package galaga.pages.editor.menu;

import engine.elements.page.Page;
import engine.elements.ui.select.TextSelect;
import engine.resource.sound.Sound;
import engine.utils.Position;
import engine.utils.Size;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaPage;
import galaga.entities.sky.Sky;
import galaga.pages.menu.MenuModeOption;
import galaga.pages.menu.MenuOption;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

public class EditorMenu extends Page<GalagaPage> {

    TextSelect editSelect;
    private Font titleFont;
    private Sky sky;

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

        this.sky = new Sky(Config.SIZE_SKY_GRID);
        if (!this.sky.init()) {
            return false;
        }

        String[] editSelectOption = new String[MenuEditorSelectOption.values().length];
        for (int i = 0; i < MenuEditorSelectOption.values().length; i++) {
            editSelectOption[i] = MenuEditorSelectOption.values()[i].toString();
        }
        this.editSelect = new TextSelect(
                editSelectOption,
                0,
                true,
                Color.WHITE, this.titleFont);
        if (!this.editSelect.init()) {
            return false;
        }
        this.editSelect.setPosition(Position.of(
                this.size.getWidth() / 2,
                this.size.getHeight() / 2));


        return true;
    }

    @Override
    public boolean onDeactivate() {
        return true;
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
        } else if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_ENTER)) {
            this.selectSound.play(2.f);
            switch (this.option) {
                case QUIT -> Galaga.getContext().getApplication().stop();
                case GAMEMODE -> {

                    if (this.gameMode.getSelected().getText().equals(MenuModeOption.SOLO.toString())) {
                        Galaga.getContext().getState().shipSkin = this.shipSelect.getSelected().getSprite();
                        Galaga.getContext().getApplication().setCurrentPage(GalagaPage.GAME_SOLO);

                    } else if (this.gameMode.getSelected().getText().equals(MenuModeOption.EDITOR.toString())) {
                        Galaga.getContext().getApplication().setCurrentPage(GalagaPage.EDITOR_MENU);
                    }
                }
                default -> {
                }
            }
        }
    }

    @Override
    public void draw() {
        this.editSelect.draw();
        // < level, sprite, settings>
        // back
    }

}
