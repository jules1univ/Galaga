package game.entities.enemies;

import engine.entity.SpriteEntity;
import game.Config;

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
        this.scale = Config.DEFAULT_SPRITE_SCALE;

        this.angle = 180.f;

        this.lockX = lockX;
        this.lockY = lockY;

        this.speed = speed;
        this.value = value;

        // TODO: create an animation system for enemies
        // split the enemies in 2 groupes (left/right) and animate them accordingly
        // animate them until they reach their lock position
        // for every new level
    }

    @Override
    public boolean init() {
        String name = this.type.name().toLowerCase();
        String path = String.format(Config.ENEMY_BASE_PATH, name);

        this.sprite = this.loadFromSprite(name, path);
        return this.sprite != null;
    }

    @Override
    public void draw() {
        super.draw();
    }
}
