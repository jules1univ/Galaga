package game.entities.ui;

import engine.entity.Entity;
import engine.graphics.sprite.Sprite;
import engine.graphics.sprite.SpriteManager;
import game.Config;
import game.Galaga;

public class FUD extends Entity {

    private Sprite ship;
    private int shipHalfScaledHeight;
    private int shipScaledWidth;

    public FUD() {

    }

    @Override
    public boolean init() {

        if (!SpriteManager.getInstance().load(Config.SHIP_SPRITE_NAME, Config.SHIP_PATH)) {
            return false;
        }
        this.ship = SpriteManager.getInstance().get(Config.SHIP_SPRITE_NAME);

        this.height = Config.FUD_HEIGHT;
        this.width = Galaga.getContext().getFrame().getWidth();

        this.x = 0;
        this.y = Galaga.getContext().getFrame().getHeight() - this.height;

        this.shipScaledWidth = (int) (this.ship.getWidth() * Config.DEFAULT_SPRITE_ICON_DOWNSCALE_FACTOR);
        this.shipHalfScaledHeight = (int) (this.ship.getHeight() * Config.DEFAULT_SPRITE_ICON_DOWNSCALE_FACTOR);

        return true;
    }

    @Override
    public void update(double dt) {
    }

    @Override
    public void draw() {
        int life = Galaga.getContext().getState().player.getLife();
        
        // TODO: maybe inline this to save performance?
        int margin = 20;
        int space = this.shipScaledWidth + margin;
        for (int i = 0; i < life; i++) {
            Galaga.getContext().getRenderer().drawSprite(this.ship, this.x + space, this.y + this.shipHalfScaledHeight,
                    Config.DEFAULT_SPRITE_ICON_DOWNSCALE_FACTOR);
            space += this.shipScaledWidth + margin;
        }

    }

}
