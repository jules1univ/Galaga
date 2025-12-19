package galaga.pages.editor.menu;

import engine.elements.page.Page;
import engine.elements.ui.select.TextSelect;
import engine.utils.Position;
import engine.utils.Size;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaPage;
import galaga.entities.sky.Sky;
import java.awt.Color;
import java.awt.Font;

public class EditorMenu extends Page<GalagaPage> {

    TextSelect editSelect;
    private Font titleFont;
    private Sky sky;

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

        String[] editSelectOption = new String[MenuEditOption.values().length];
        for (int i = 0; i < MenuEditOption.values().length; i++) {
            editSelectOption[i] = MenuEditOption.values()[i].toString();
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
    }

    @Override
    public void draw() {
        this.editSelect.draw();
        // < level, sprite, settings>
        // back
    }

}
