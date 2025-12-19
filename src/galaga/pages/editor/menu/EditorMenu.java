package galaga.pages.editor.menu;

import engine.elements.page.Page;
import engine.elements.ui.select.TextSelect;
import engine.utils.Position;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaPage;
import galaga.pages.menu.MenuModeOption;
import java.awt.Color;
import java.awt.Font;

public class EditorMenu extends Page<GalagaPage> {

    TextSelect editSelect;
    private Font titleFont;

    public EditorMenu() {
        super(GalagaPage.EDITOR_MENU);
    }

    @Override
    public boolean onActivate() {

        this.titleFont = Galaga.getContext().getResource().get(Config.FONTS, Config.VARIANT_FONT_XLARGE);

        String[] editSelectOptions = new String[editSelectOption.values().length];
        for (int i = 0; i < editSelectOption.values().length; i++) {
            editSelectOptions[i] = editSelectOption.values()[i].toString();
        }
        this.editSelect = new TextSelect(
                editSelectOptions,
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
    }

    @Override
    public void draw() {
        // < level, sprite, settings>
        // back
    }

}
