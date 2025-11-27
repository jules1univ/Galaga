package game.entities.player;

import java.awt.event.KeyEvent;

import engine.entity.Direction;
import engine.entity.SpriteEntity;
import game.Game;

public class Player extends SpriteEntity<PlayerType> {
    private static final float PLAYER_SPEED = 200.f;

    public Player(engine.AppContext ctx) {
        super(ctx);
        this.type = PlayerType.DEFAULT;
        this.direction = Direction.UP;
    }

    @Override
    public boolean init() {
        this.sprite = this.loadFromSprite("ship", ".\\resources\\sprites\\ship.spr", Game.DEFAULT_SPRITE_SCALE);

        this.x = (this.ctx.frame.getWidth() - this.sprite.getWidth()) / 2;
        this.y = this.ctx.frame.getHeight() - this.sprite.getHeight() - 10;

        return this.sprite != null;
    }

    @Override
    public void update(double dt) {
        if (this.ctx.input.isKeyDown(KeyEvent.VK_LEFT)) {
            this.x -= PLAYER_SPEED * dt;
        }

        if (this.ctx.input.isKeyDown(KeyEvent.VK_RIGHT)) {
            this.x += PLAYER_SPEED * dt;
        }

        if (this.ctx.input.isKeyDown(KeyEvent.VK_SPACE)) {

        }
    }

}
