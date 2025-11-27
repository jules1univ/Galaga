package game.entities.player;

import java.awt.event.KeyEvent;

import engine.entity.Direction;
import engine.entity.SpriteEntity;
import game.Galaga;

public class Player extends SpriteEntity {
    private static final float PLAYER_SPEED = 600.f;

    public Player() {
        super();
        this.direction = Direction.UP;
    }

    @Override
    public boolean init() {
        this.sprite = this.loadFromSprite("ship", ".\\resources\\sprites\\ship.spr", Galaga.DEFAULT_SPRITE_SCALE);

        this.x = (Galaga.getContext().getFrame().getWidth() - this.sprite.getWidth()) / 2;
        this.y = Galaga.getContext().getFrame().getHeight() - this.sprite.getHeight() - 10;

        return this.sprite != null;
    }

    @Override
    public void update(double dt) {
        if (Galaga.getContext().getInput().isKeyDown(KeyEvent.VK_LEFT)) {
            this.x -= PLAYER_SPEED * dt;
        }

        if (Galaga.getContext().getInput().isKeyDown(KeyEvent.VK_RIGHT)) {
            this.x += PLAYER_SPEED * dt;
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
