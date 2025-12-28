package galaga.pages.editor.level;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import engine.graphics.Renderer;
import galaga.GalagaPage;

public class LevelEditor extends Page<GalagaPage> {
    
    public LevelEditor() {
        super(GalagaPage.EDITOR_LEVEL);
    }

    @Override
    public boolean onActivate() {
        this.state = PageState.ACTIVE;
        return true;
    }

    @Override
    public boolean onDeactivate() {
        this.state = PageState.INACTIVE;
        return true;
    }

    
    @Override
    public void onReceiveArgs(Object... args) {
    }

    @Override
    public void update(float dt) {
    }

    @Override
    public void draw(Renderer renderer) {
    }


}
