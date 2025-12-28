package galaga.pages.editor.settings;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import engine.graphics.Renderer;
import galaga.GalagaPage;

public class Settings extends Page<GalagaPage> {

    public Settings() {
        super(GalagaPage.EDITOR_SETTINGS);
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
    public void onReceiveArgs(Object... args) {
    }

    @Override
    public void update(float dt) {
    }

    @Override
    public void draw(Renderer renderer) {
    }
    
}
