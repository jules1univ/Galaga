package galaga.pages.editor.menu;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import engine.elements.ui.select.TextSelectEnum;
import engine.elements.ui.text.Text;
import engine.elements.ui.text.TextPosition;
import engine.resource.sound.Sound;
import engine.utils.Position;
import engine.utils.Size;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaPage;
import galaga.GalagaSound;
import galaga.entities.sky.Sky;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

public class EditorMenu extends Page<GalagaPage> {

    private TextSelectEnum<EditorMenuModeOption> editSelect;
    private Text back;

    private Font titleFont;
    private Sky sky;

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

        int margin = 50;

        this.sky = new Sky(Config.SIZE_SKY_GRID);
        if (!this.sky.init()) {
            return false;
        }

        this.editSelect = new TextSelectEnum<>(
                EditorMenuModeOption.class,
                0,
                true,
                Color.WHITE, this.titleFont);
        if (!this.editSelect.init()) {
            return false;
        }
        this.editSelect.setPosition(Position.of(
                this.size.getWidth() / 2,
                this.size.getHeight() / 2));

        this.back = new Text(
                "BACK",
                Position.of(
                        this.size.getWidth() / 2,
                        this.editSelect.getPosition().getY() + this.editSelect.getSize().getHeight() + margin),
                Color.WHITE, this.titleFont);
        if (!this.back.init()) {
            return false;
        }
        this.back.setCenter(TextPosition.CENTER, TextPosition.END);

        this.option = EditorMenuOption.EDITSELECT;
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
    public void update(float dt) {
        this.sky.update(dt);

        if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_LEFT)) {
            this.selectSound.play(2.f);
            switch (this.option) {
                case EDITSELECT -> this.editSelect.prev();
                default -> {
                }
            }
            this.selectSound.play(2.f);
        } else if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_RIGHT)) {
            this.selectSound.play(2.f);
            switch (this.option) {
                case EDITSELECT -> this.editSelect.next();
                default -> {
                }
            }
        } else if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_UP)) {
            this.selectSound.play(2.f);
            switch (this.option) {
                case EDITSELECT -> {
                    this.option = EditorMenuOption.BACK;
                    this.updateMenuSelect();
                }
                case BACK -> {
                    this.option = EditorMenuOption.EDITSELECT;
                    this.updateMenuSelect();
                }
            }
        } else if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_DOWN)) {
            this.selectSound.play(2.f);
            switch (this.option) {
                case EDITSELECT -> {
                    this.option = EditorMenuOption.BACK;
                    this.updateMenuSelect();

                }

                case BACK -> {
                    this.option = EditorMenuOption.EDITSELECT;
                    this.updateMenuSelect();
                }
            }
        } else if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_ENTER)) {
            this.selectSound.play(2.f);
            switch (this.option) {
                case BACK -> Galaga.getContext().getApplication().setCurrentPage(GalagaPage.MAIN_MENU);
                case EDITSELECT -> {
                    switch (this.editSelect.getSelectedOption()) {
                        case LEVEL -> Galaga.getContext().getApplication().setCurrentPage(GalagaPage.EDITOR_LEVEL);
                        case SPRITE -> Galaga.getContext().getApplication().setCurrentPage(GalagaPage.EDITOR_SPRITE);
                        case SETTINGS -> Galaga.getContext().getApplication().setCurrentPage(GalagaPage.EDITOR_SETTINGS);
                    }
                }
            }
        }
    }

    private void updateMenuSelect() {
        switch (this.option) {
            case EDITSELECT -> {
                this.editSelect.setShowArrows(true);
                this.editSelect.setColor(Color.ORANGE);
                this.back.setColor(Color.WHITE);
            }
            case BACK -> {
                this.editSelect.setShowArrows(false);
                this.editSelect.setColor(Color.WHITE);
                this.back.setColor(Color.ORANGE);
            }
        }
    }

    @Override
    public void draw() {
        this.sky.draw();
        this.editSelect.draw();
        this.back.draw();
    }

}
