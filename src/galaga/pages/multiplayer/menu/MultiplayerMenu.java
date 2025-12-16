package galaga.pages.multiplayer.menu;

import engine.elements.page.Page;
import galaga.GalagaPage;

public class MultiplayerMenu extends Page<GalagaPage> {

    public MultiplayerMenu() {
        super(GalagaPage.MULTIPLAYER_MENU);
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
    }

}
