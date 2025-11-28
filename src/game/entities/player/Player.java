package game.entities.player;

import java.awt.event.KeyEvent;

import engine.entity.SpriteEntity;
import game.Config;
import game.Galaga;

public class Player extends SpriteEntity {

    public Player() {
        super();
        this.angle = 0.f;
    }

    @Override
    public boolean init() {
        this.sprite = this.loadFromSprite("ship", Config.SHIP_PATH, Config.DEFAULT_SPRITE_SCALE);

        this.x = (Galaga.getContext().getFrame().getWidth() - this.sprite.getWidth()) / 2;
        this.y = Galaga.getContext().getFrame().getHeight() - this.sprite.getHeight() - 10;

        return this.sprite != null;
    }

    @Override
    public void update(double dt) {
        if (Galaga.getContext().getInput().isKeyDown(KeyEvent.VK_LEFT)) {
            this.x -= Config.PLAYER_SPEED * dt;
        }

        if (Galaga.getContext().getInput().isKeyDown(KeyEvent.VK_RIGHT)) {
            this.x += Config.PLAYER_SPEED * dt;
        }

        this.x = Math.clamp(this.x, this.sprite.getWidth(),
                Galaga.getContext().getFrame().getWidth() - this.sprite.getWidth());

        if (Galaga.getContext().getInput().isKeyDown(KeyEvent.VK_SPACE)) {

        }
    }

    @Override
    public void draw() {
        super.draw();
    }

}
