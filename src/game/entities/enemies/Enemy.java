package game.entities.enemies;

import engine.entity.Direction;
import engine.entity.SpriteEntity;
import game.Galaga;

public abstract class Enemy extends SpriteEntity {

    private static final float DEFAULT_ENEMY_START_X = 350.0f;
    private static final float DEFAULT_ENEMY_START_Y = -50.0f;

    protected float hitBoxSize;
    protected float speed;
    protected int value;
    protected EnemyType type;

    protected float endX;
    protected float endY;

    public Enemy(EnemyType type, float endX, float endY, float size, int value,
            float speed) {
        super();
        this.direction = Direction.DOWN;
        this.type = type;

        this.x = DEFAULT_ENEMY_START_X;
        this.y = DEFAULT_ENEMY_START_Y;

        this.endX = endX;
        this.endY = endY;

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

    @Override
    public void update(double dt) {
        float directionX = this.endX - this.x;
        float directionY = this.endY - this.y;
        float length = (float) Math.sqrt(directionX * directionX + directionY * directionY);

        if (length != 0) {
            directionX /= length;
            directionY /= length;
        }

        this.x += directionX * this.speed * dt;
        this.y += directionY * this.speed * dt;
    }

    @Override
    public void draw() {
        super.draw();
    }

}
