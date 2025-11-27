package game.entities.enemies;

import engine.entity.Direction;
import engine.entity.SpriteEntity;
import game.Galaga;

public abstract class Enemy extends SpriteEntity {

    protected float hitBoxSize;
    protected float speed;
    protected int value;
    protected EnemyType type;

    public Enemy(EnemyType type, float startX, float startY, float size, int value,
            float speed) {
        super();
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

        this.sprite = this.loadFromSprite(name, path, Galaga.DEFAULT_SPRITE_SCALE);
        return this.sprite != null;
    }
}
