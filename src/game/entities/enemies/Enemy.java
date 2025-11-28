package game.entities.enemies;

import engine.entity.SpriteEntity;
import game.Galaga;

public abstract class Enemy extends SpriteEntity {

    protected float speed;
    protected int value;
    protected EnemyType type;

    protected float lockX;
    protected float lockY;

    public Enemy(EnemyType type, float lockX, float lockY, int value,
            float speed) {
        super();
        this.type = type;

        this.angle = 180.f;
        this.x = -(float) Math.random() * Galaga.getContext().getFrame().getWidth();
        this.y = -(float) Math.random() * Galaga.getContext().getFrame().getHeight();

        this.lockX = lockX;
        this.lockY = lockY;

        this.speed = speed;
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
        float directionX = this.lockX - this.x;
        float directionY = this.lockY - this.y;
        float length = (float) Math.sqrt(directionX * directionX + directionY * directionY);

        if (length <= 3.f) {
            this.x = this.lockX;
            this.y = this.lockY;
            this.angle = 180.f;
            return;
        }

        this.x += directionX * this.speed * dt;
        this.y += directionY * this.speed * dt;
    }

    @Override
    public void draw() {
        super.draw();
    }

}
