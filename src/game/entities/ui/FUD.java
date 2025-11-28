package game.entities.ui;

import engine.entity.Entity;
import engine.graphics.sprite.Sprite;
import engine.graphics.sprite.SpriteManager;
import game.Config;
import game.Galaga;

public class FUD extends Entity {

    private Sprite ship;

    public FUD() {

    }

    @Override
    public boolean init() {

        if(!SpriteManager.getInstance().load(Config.SHIP_SPRITE_NAME, Config.SHIP_PATH))
        {
            return false;
        }
        this.ship = SpriteManager.getInstance().get(Config.SHIP_SPRITE_NAME);        


        this.height = Config.FUD_HEIGHT;
        this.width = Galaga.getContext().getFrame().getWidth();

        this.x = 0;
        this.y = Galaga.getContext().getFrame().getHeight() - this.height;
        return true;
    }

    @Override
    public void update(double dt) {
    }

    @Override
    public void draw() {
        // TODO: load number of life from global state => player
        int margin = 10;
        int space = margin;

        for (int i = 0; i < 3; i++) {
            Galaga.getContext().getRenderer()
            .drawSprite(this.ship,this.x + space, this.y - this.ship.getHeight()/2, Config.DEFAULT_SPRITE_SCALE/2);
            space += this.ship.getWidth() * (Config.DEFAULT_SPRITE_SCALE/2) + margin;
        }
    }

}
