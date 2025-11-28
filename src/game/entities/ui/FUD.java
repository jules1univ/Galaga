package game.entities.ui;

import engine.entity.Entity;
import engine.graphics.sprite.Sprite;
import engine.graphics.sprite.SpriteManager;
import game.Config;
import game.Galaga;

public class FUD extends Entity {

    private Sprite ship;
    private Sprite medal;

    private int shipScaledHeight;
    private int shipScaledWidth;

    private int medalScaledHeight;
    private int medalScaledWidth;

    public FUD() {

    }

    @Override
    public boolean init() {

        if (!SpriteManager.getInstance().load(Config.SHIP_SPRITE_NAME, Config.SHIP_PATH, Config.DEFAULT_SPRITE_SCALE)) {
            return false;
        }
        this.ship = SpriteManager.getInstance().get(Config.SHIP_SPRITE_NAME);
        this.shipScaledWidth = (int) (this.ship.getWidth() * Config.DEFAULT_SPRITE_ICON_DOWNSCALE_FACTOR);
        this.shipScaledHeight = (int) (this.ship.getHeight() * Config.DEFAULT_SPRITE_ICON_DOWNSCALE_FACTOR);
        if (!SpriteManager.getInstance().load(Config.MEDAL_SPRITE_NAME, Config.MEDAL_PATH, Config.DEFAULT_SPRITE_SCALE)) {
            return false;
        }
        this.medal = SpriteManager.getInstance().get(Config.MEDAL_SPRITE_NAME);
        this.medalScaledWidth = (int) (this.medal.getWidth() * Config.DEFAULT_SPRITE_ICON_DOWNSCALE_FACTOR);
        this.medalScaledHeight = (int) (this.medal.getHeight() * Config.DEFAULT_SPRITE_ICON_DOWNSCALE_FACTOR);

        this.height = Config.FUD_HEIGHT;
        this.width = Galaga.getContext().getFrame().getWidth();

        this.x = 0;
        this.y = Galaga.getContext().getFrame().getHeight() - this.height;

        return true;
    }

    @Override
    public void update(double dt) {
    }

    private void drawPlayerIcons(int length, int width, int height, Sprite sprite, boolean fromRight) {
        int margin = width/2;
        int space = width;

        for (int i = 0; i < length; i++) {
            float x = Math.abs((fromRight ? this.width : 0 ) - (this.x + width + space));
            float y = this.y + height/2;
            Galaga.getContext().getRenderer().drawSprite(sprite, x, y, Config.DEFAULT_SPRITE_ICON_DOWNSCALE_FACTOR);
            space += width + margin;
        }
    }


    @Override
    public void draw() {
        this.drawPlayerIcons(Galaga.getContext().getState().player.getLife(), this.shipScaledWidth, this.shipScaledHeight, this.ship, false);
        this.drawPlayerIcons(Galaga.getContext().getState().player.getCompletedLevels(), this.medalScaledWidth, this.medalScaledHeight, this.medal, true);
    }

}
