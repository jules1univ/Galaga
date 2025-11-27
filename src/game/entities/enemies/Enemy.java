package game.entities.enemies;

import engine.AppContext;
import engine.entity.Direction;
import engine.entity.SpriteEntity;
import game.Game;

public abstract class Enemy extends SpriteEntity<EnemyType> {

    protected float hitBoxSize;
    protected float speed;
    protected int value;

    public Enemy(AppContext ctx, EnemyType type, float startX, float startY, float size, int value, float speed) {
        super(ctx);
        this.direction = Direction.DOWN;
        this.type = type;

        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.hitBoxSize = size;

        this.value = value;
    }

    @Override
    public boolean init() {
        String name = this.type.name().toLowerCase();
        String path = String.format(".\\resources\\sprites\\%s.spr", name);

        this.sprite = this.loadFromSprite(name, path, Game.DEFAULT_SPRITE_SCALE);
        return this.sprite != null;
    }
}
