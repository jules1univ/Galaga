package galaga.pages.editor.menu;

import engine.elements.page.Page;
import galaga.GalagaPage;

public class EditorMenu extends Page<GalagaPage> {

    public EditorMenu() {
        super(GalagaPage.EDITOR_MENU);
    }

    @Override
    public boolean onActivate() {
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
