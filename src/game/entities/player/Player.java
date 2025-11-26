package game.entities.player;

import java.awt.event.KeyEvent;

import engine.entity.Entity;
import engine.graphics.sprite.Sprite;
import engine.graphics.sprite.SpriteEntity;
import game.Game;

public class Player extends Entity<PlayerType> implements SpriteEntity {
    private Sprite sprite;
    private static final double PLAYER_SPEED = 200.0;

    public Player(engine.AppContext ctx) {
        super(ctx);
        this.type = PlayerType.DEFAULT;
    }

    @Override
    public Sprite getSprite() {
        return this.sprite;
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

    @Override
    public void draw() {
        this.ctx.renderer.drawSpriteEntity(this);
    }

}
