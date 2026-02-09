package galaga.pages.multiplayer.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.List;

import engine.elements.page.Page;
import engine.elements.ui.Alignment;
import engine.elements.ui.input.Input;
import engine.elements.ui.text.Text;
import engine.graphics.Renderer;
import engine.graphics.sprite.Sprite;
import engine.resource.sound.Sound;
import engine.utils.Position;
import engine.utils.Size;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaPage;
import galaga.entities.sky.Sky;
import galaga.resources.sound.GalagaSound;

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

    private Sound themeSound;
    private Sound selectSound;
    private Sound keyboardSound;

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

        this.themeSound = Galaga.getContext().getResource().get(GalagaSound.menu_theme);
        if (this.themeSound == null) {
            return false;
        }
        this.themeSound.setLoop(true);
        this.themeSound.play(0.2f);

        this.selectSound = Galaga.getContext().getResource().get(GalagaSound.menu_select);
        if (this.selectSound == null) {
            return false;
        }
        this.selectSound.setCapacity(4);

        this.keyboardSound = Galaga.getContext().getResource().get(GalagaSound.menu_keyboard);
        if (this.keyboardSound == null) {
            return false;
        }
        this.keyboardSound.setCapacity(4);

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
        this.username.setCenter(Alignment.CENTER, Alignment.CENTER);

        this.ip = new Input(Position.of(
                Config.WINDOW_WIDTH / 2.f,
                this.username.getPosition().getY() + padding + this.username.getSize().getHeight()),
                Config.WINDOW_WIDTH / 2.f,
                "Server X.X.X.X:YY",
                Color.WHITE, this.textFont);
        if (!this.ip.init()) {
            return false;
        }

        this.ip.setCenter(Alignment.CENTER, Alignment.CENTER);

        this.next = new Text("Next", Position.of(
                this.ip.getPosition().getX() + this.ip.getSize().getWidth(),
                this.ip.getPosition().getY() + padding * 2),
                Color.WHITE, this.textFont);

        if (!this.next.init()) {
            return false;
        }
        this.next.setCenter(Alignment.END, Alignment.END);

        this.back = new Text("Back", Position.of(
                this.ip.getPosition().getX(),
                this.ip.getPosition().getY() + padding * 2),
                Color.WHITE, this.textFont);

        if (!this.back.init()) {
            return false;
        }
        this.back.setCenter(Alignment.BEGIN, Alignment.END);

        this.option = MultiplayerMenuOption.USERNAME;
        this.updateMenuSelect();
        return true;
    }

    @Override
    public boolean onDeactivate() {
        this.themeSound.stop();
        this.selectSound.stop();
        return true;
    }

    @Override
    public void onReceiveArgs(Object... args) {
        if (args == null || args.length != 2) {
            return;
        }

        this.username.setText((String) args[0]);
        this.ip.setText((String) args[1]);
    }

    @Override
    public void update(float dt) {

        if (Galaga.getContext().getInput().isTyping()) {
            switch (this.option) {
                case IP, USERNAME -> {
                    this.keyboardSound.play(.5f);
                }
                default -> {
                }
            }
        }

        if (Galaga.getContext().getInput().isKeyPressed(
            Galaga.getContext().getState().keyboard.getKey(Config.KEYBOARD_MENU_NAVIGATE_UP).orElse(KeyEvent.VK_UP)

        )) {
            this.selectSound.play(2.f);
            switch (this.option) {
                case IP -> this.option = MultiplayerMenuOption.USERNAME;
                case NEXT -> this.option = MultiplayerMenuOption.IP;
                case BACK -> this.option = MultiplayerMenuOption.NEXT;
                case USERNAME -> this.option = MultiplayerMenuOption.BACK;
            }
            this.updateMenuSelect();
        } else if (Galaga.getContext().getInput().isKeyPressed(
            Galaga.getContext().getState().keyboard.getKeys(Config.KEYBOARD_MENU_NAVIGATE_DOWN, Config.KEYBOARD_MENU_NAVIGATE).orElse(List.of(KeyEvent.VK_DOWN, KeyEvent.VK_TAB))
        )) {
            this.selectSound.play(2.f);
            switch (this.option) {
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

        if (Galaga.getContext().getInput().isKeyPressed(
            Galaga.getContext().getState().keyboard.getKey(Config.KEYBOARD_MENU_CONFIRM).orElse(KeyEvent.VK_ENTER)

        )) {
            this.selectSound.play(2.f);
            switch (this.option) {
                case NEXT -> {
                    Galaga.getContext().getApplication().setCurrentPage(GalagaPage.MULTIPLAYER_LOBBY,
                            this.username.getText(), this.ip.getText());
                }
                case BACK -> {
                    Galaga.getContext().getApplication().setCurrentPage(GalagaPage.MAIN_MENU);
                }
                case IP, USERNAME -> {
                }
            }
        }

    }

    @Override
    public void draw(Renderer renderer) {

        this.sky.draw(renderer);

        renderer.drawSprite(this.logo, this.logoPosition, Config.SPRITE_SCALE_ICON);

        this.username.draw(renderer);
        this.ip.draw(renderer);

        this.back.draw(renderer);
        this.next.draw(renderer);
    }

}
