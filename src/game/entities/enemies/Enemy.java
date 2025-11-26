package game.entities.enemies;

import engine.AppContext;
import engine.entity.Entity;
import engine.graphics.sprite.Sprite;
import engine.graphics.sprite.SpriteEntity;
import game.Game;

public abstract class Enemy extends Entity<EnemyType> implements SpriteEntity {

    protected Sprite sprite;

    public Enemy(AppContext ctx, EnemyType type) {
        super(ctx);
        this.type = type;
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    @Override
    public boolean init() {
        String name = this.type.name().toLowerCase();
        String path = String.format(".\\resources\\sprites\\%s.spr", name);

        this.sprite = this.loadFromSprite(name, path, Game.DEFAULT_SPRITE_SCALE);
        return this.sprite != null;
    }

    @Override
    public void draw() {
        this.ctx.renderer.drawSpriteEntity(this);
    }
}
