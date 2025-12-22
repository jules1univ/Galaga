package galaga.pages.multiplayer.menu;

import java.awt.Color;
import java.awt.Font;

import engine.elements.page.Page;
import engine.elements.ui.input.Input;
import engine.utils.Position;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaPage;

public class MultiplayerMenu extends Page<GalagaPage> {

    private Input ipInput;
    private Font textFont;

    public MultiplayerMenu() {
        super(GalagaPage.MULTIPLAYER_MENU);
    }

    @Override
    public boolean onActivate() {

        this.textFont = Galaga.getContext().getResource().get(Config.FONTS, Config.VARIANT_FONT_TEXT);
        if(this.textFont == null) {
            return false;
        }

        this.ipInput =  new Input(Position.zero(), Config.WINDOW_WIDTH/4.f, "Server ip address ...", Color.WHITE, this.textFont);
        if(!this.ipInput.init()) {
            return false;
        }

        this.ipInput.setFocused(true);
        return true;
    }

    @Override
    public boolean onDeactivate() {
        return true;
    }

    @Override
    public void update(float dt) {
        this.ipInput.update(dt);
    }

    @Override
    public void draw() {
        this.ipInput.draw();
    }

}
