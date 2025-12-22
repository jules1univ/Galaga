package galaga.pages.multiplayer.menu;

import java.awt.Color;
import java.awt.Font;

import engine.elements.page.Page;
import engine.elements.ui.input.Input;
import engine.elements.ui.text.TextPosition;
import engine.utils.Position;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaPage;

public class MultiplayerMenu extends Page<GalagaPage> {

    private Input ip;
    private Input username;

    private Font textFont;

    public MultiplayerMenu() {
        super(GalagaPage.MULTIPLAYER_MENU);
    }

    @Override
    public boolean onActivate() {

        this.textFont = Galaga.getContext().getResource().get(Config.FONTS, Config.VARIANT_FONT_XLARGE);
        if (this.textFont == null) {
            return false;
        }

        float margin = 50.f;

        this.ip = new Input(Position.of(
                Config.WINDOW_WIDTH / 2.f,
                Config.WINDOW_HEIGHT / 2.f), Config.WINDOW_WIDTH / 2.f,
                "Server X.X.X.X:YY",
                Color.WHITE, this.textFont);
        if (!this.ip.init()) {
            return false;
        }

        this.ip.setCenter(TextPosition.CENTER, TextPosition.CENTER);
        this.ip.setFocused(false);

        this.username = new Input(Position.of(
                Config.WINDOW_WIDTH / 2.f,
                this.ip.getPosition().getY() - margin), Config.WINDOW_WIDTH / 2.f, "Username", Color.WHITE,
                this.textFont);
        if (!this.username.init()) {
            return false;
        }
        this.username.setCenter(TextPosition.CENTER, TextPosition.CENTER);
        this.username.setFocused(true);

        return true;
    }

    @Override
    public boolean onDeactivate() {
        return true;
    }

    @Override
    public void update(float dt) {
        this.ip.update(dt);
        this.username.update(dt);
    }

    @Override
    public void draw() {
        this.username.draw();
        this.ip.draw();
    }

}
