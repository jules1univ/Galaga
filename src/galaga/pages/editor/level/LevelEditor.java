package galaga.pages.editor.level;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import galaga.GalagaPage;

public class LevelEditor extends Page<GalagaPage> {
    
    public LevelEditor() {
        super(GalagaPage.EDITOR_LEVEL);
    }

    @Override
    public boolean onActivate() {
        return true;
    }

    @Override
    public boolean onDeactivate() {
        this.state = PageState.INACTIVE;
        return true;
    }

    @Override
    public void update(float dt) {
    }

    @Override
    public void draw() {
    }


}
