package game.entities.ui;

import java.util.ArrayList;
import java.util.List;

import engine.graphics.sprite.Sprite;
import engine.graphics.sprite.SpriteManager;
import engine.ui.UIElement;
import engine.ui.icon.Icon;
import engine.ui.icon.IconGroup;
import game.Config;
import game.Galaga;

public class FUD extends UIElement {

    private IconGroup lifeIcons;
    private IconGroup medalIcons;

    private Sprite ship;
    private Sprite medal;

    public FUD() {

    }

    private List<Icon> createArrayIcon(Sprite sprite, int length) {
        List<Icon> icons = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            Icon icon = new Icon(sprite, Config.DEFAULT_SPRITE_ICON_DOWNSCALE_FACTOR);
            icon.init();
            icons.add(icon);
        }
        return icons;
    }

    @Override
    public boolean init() {
        this.height = Config.FUD_HEIGHT;
        this.width = Galaga.getContext().getFrame().getWidth();

        this.x = 0;
        this.y = Galaga.getContext().getFrame().getHeight() - this.height;

        if (!SpriteManager.getInstance().load(Config.SHIP_SPRITE_NAME, Config.SHIP_PATH, Config.DEFAULT_SPRITE_SCALE)) {
            return false;
        }

        this.ship = SpriteManager.getInstance().get(Config.SHIP_SPRITE_NAME);
        this.lifeIcons = new IconGroup(new ArrayList<>(), this.width, true, (int) this.ship.getWidth() / 2);
        this.lifeIcons.setPosition(this.x, this.y);

        if (!this.lifeIcons.init()) {
            return false;
        }

        if (!SpriteManager.getInstance().load(Config.MEDAL_SPRITE_NAME, Config.MEDAL_PATH,
                Config.DEFAULT_SPRITE_SCALE)) {
            return false;
        }

        this.medal = SpriteManager.getInstance().get(Config.MEDAL_SPRITE_NAME);
        this.medalIcons = new IconGroup(new ArrayList<>(), this.width, false, (int) this.medal.getWidth() / 2);
        this.medalIcons.setPosition(this.x, this.y);

        if (!this.medalIcons.init()) {
            return false;
        }

        return true;
    }

    @Override
    public void update(double dt) {
        int lifes = Galaga.getContext().getState().player.getLife();
        if (this.lifeIcons.getIcons().size() != lifes) {
            this.lifeIcons.setIcons(createArrayIcon(this.ship, lifes));
        }

        int medals = Galaga.getContext().getState().player.getMedals();
        if (this.medalIcons.getIcons().size() != medals) {
            this.medalIcons.setIcons(createArrayIcon(this.medal, medals));
        }
    }

    @Override
    public void draw() {
        this.lifeIcons.draw();
        this.medalIcons.draw();
    }

}
