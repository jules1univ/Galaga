package galaga.pages.multiplayer.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

import engine.elements.page.Page;
import engine.elements.ui.input.Input;
import engine.elements.ui.text.Text;
import engine.elements.ui.text.TextPosition;
import engine.graphics.sprite.Sprite;
import engine.utils.Position;
import engine.utils.Size;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaPage;
import galaga.entities.sky.Sky;

public class MultiplayerMenu extends Page<GalagaPage> {

    private Input ip;
    private Input username;
    private Text next;
    private Text back;

    private MultiplayerMenuOption option;

    private Font textFont;

    private Sky sky;

    private Sprite logo;
    private Position logoPosition;

    public MultiplayerMenu() {
        super(GalagaPage.MULTIPLAYER_MENU);
    }

    private void updateMenuSelect() {
        switch (this.option) {
            case USERNAME -> {
                this.username.setFocused(true);
                this.username.setColor(Color.ORANGE);

                this.ip.setFocused(false);
                this.ip.setColor(Color.WHITE);
                this.next.setColor(Color.WHITE);
                this.back.setColor(Color.WHITE);
            }
            case IP -> {
                this.username.setFocused(false);
                this.username.setColor(Color.WHITE);

                this.ip.setFocused(true);
                this.ip.setColor(Color.ORANGE);

                this.back.setColor(Color.WHITE);
                this.next.setColor(Color.WHITE);
            }
            case NEXT -> {
                this.username.setFocused(false);
                this.username.setColor(Color.WHITE);

                this.ip.setFocused(false);
                this.ip.setColor(Color.WHITE);

                this.next.setColor(Color.ORANGE);
                this.back.setColor(Color.WHITE);
            }
            case BACK -> {
                this.username.setFocused(false);
                this.username.setColor(Color.WHITE);

                this.ip.setFocused(false);
                this.ip.setColor(Color.WHITE);

                this.next.setColor(Color.WHITE);
                this.back.setColor(Color.ORANGE);
            }
        }
    }

    @Override
    public boolean onActivate() {
        this.size = Size.of(
                Galaga.getContext().getFrame().getWidth(),
                Galaga.getContext().getFrame().getHeight());

        this.sky = new Sky(Config.SIZE_SKY_GRID);
        if (!this.sky.init()) {
            return false;
        }

        this.textFont = Galaga.getContext().getResource().get(Config.FONTS, Config.VARIANT_FONT_XLARGE);
        if (this.textFont == null) {
            return false;
        }

        float padding = 50.f;

        this.logo = Galaga.getContext().getResource().get(Config.SPRITE_LOGO);
        this.logoPosition = Position.of(
                (this.size.getWidth()) / 2,
                (this.size.getHeight() - this.logo.getSize().getHeight() * Config.SPRITE_SCALE_ICON) / 2 - padding
                        - padding / 2);

        this.username = new Input(Position.of(
                Config.WINDOW_WIDTH / 2.f,
                this.logoPosition.getY() + logo.getSize().getHeight() + padding), Config.WINDOW_WIDTH / 2.f,
                "Username", Color.WHITE,
                this.textFont);
        if (!this.username.init()) {
            return false;
        }
        this.username.setCenter(TextPosition.CENTER, TextPosition.CENTER);

        this.ip = new Input(Position.of(
                Config.WINDOW_WIDTH / 2.f,
                this.username.getPosition().getY() + padding + this.username.getSize().getHeight()),
                Config.WINDOW_WIDTH / 2.f,
                "Server X.X.X.X:YY",
                Color.WHITE, this.textFont);
        if (!this.ip.init()) {
            return false;
        }

        this.ip.setCenter(TextPosition.CENTER, TextPosition.CENTER);

        this.next = new Text("Next", Position.of(
                Config.WINDOW_WIDTH / 2.f,
                this.ip.getPosition().getY() + padding + this.ip.getSize().getHeight()),
                Color.WHITE, this.textFont);

        if (!this.next.init()) {
            return false;
        }
        this.next.setCenter(TextPosition.CENTER, TextPosition.CENTER);

        this.back = new Text("Back", Position.of(
                Config.WINDOW_WIDTH / 2.f,
                this.next.getPosition().getY() + padding + this.next.getSize().getHeight()),
                Color.WHITE, this.textFont);

        if (!this.back.init()) {
            return false;
        }
        this.back.setCenter(TextPosition.CENTER, TextPosition.CENTER);


        this.option = MultiplayerMenuOption.USERNAME;
        this.updateMenuSelect();
        return true;
    }

    @Override
    public boolean onDeactivate() {
        return true;
    }

    @Override
    public void update(float dt) {
        if(Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_UP))
        {
            switch(this.option)
            {
                case IP -> this.option = MultiplayerMenuOption.USERNAME;
                case NEXT -> this.option = MultiplayerMenuOption.IP;
                case BACK -> this.option = MultiplayerMenuOption.NEXT;
                case USERNAME -> this.option = MultiplayerMenuOption.BACK;
            }
            this.updateMenuSelect();
        }
        else if(Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_DOWN))
        {
            switch(this.option)
            {
                case USERNAME -> this.option = MultiplayerMenuOption.IP;
                case IP -> this.option = MultiplayerMenuOption.NEXT;
                case NEXT -> this.option = MultiplayerMenuOption.BACK;
                case BACK -> this.option = MultiplayerMenuOption.USERNAME;
            }
            this.updateMenuSelect();
        }

        this.ip.update(dt);
        this.username.update(dt);

        this.sky.update(dt);
    }

    @Override
    public void draw() {

        this.sky.draw();

        Galaga.getContext().getRenderer().drawSprite(this.logo, this.logoPosition, Config.SPRITE_SCALE_ICON);

        this.username.draw();
        this.ip.draw();

        this.back.draw();
        this.next.draw();
    }

}
