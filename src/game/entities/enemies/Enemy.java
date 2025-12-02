package game.entities.enemies;

import engine.elements.entity.SpriteEntity;
import engine.utils.Position;
import game.Config;
import game.Galaga;

public abstract class Enemy extends SpriteEntity {

    protected float speed;
    protected int value;
    protected final EnemyType type;
    protected final boolean leftAnimation;

    protected Position lock;
    protected boolean inLock;

    public Enemy(EnemyType type, boolean leftAnimation, Position lock, int value,
            float speed) {
        super();
        this.type = type;

        this.angle = 180.f;
        this.scale = Config.SPRITE_SCALE_DEFAULT;

        this.position = leftAnimation ? Config.POSITION_ENEMY_LEFT.copy() : Config.POSITION_ENEMY_RIGHT.copy();

        this.lock = lock.copy();
        this.leftAnimation = leftAnimation;
        this.inLock = false;

        this.speed = speed;
        this.value = value;

        // TODO: create an animation system for enemies
        // split the enemies in 2 groupes (left/right) and animate them accordingly
        // animate them until they reach their lock position
        // for every new level
    }

    @Override
    public boolean init() {
        this.sprite = Galaga.getContext().getResource().get(this.type);
        if (this.sprite == null) {
            return false;
        }
        this.size = this.sprite.getSize();
        return true;
    }

    @Override
    public void draw() {
        super.draw();
    }
}
