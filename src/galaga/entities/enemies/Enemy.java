package galaga.entities.enemies;

import engine.Application;
import engine.elements.entity.SpriteEntity;
import engine.resource.sound.Sound;
import engine.utils.Collision;
import engine.utils.Position;
import engine.utils.Size;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaSound;
import galaga.entities.bullet.Bullet;
import galaga.entities.bullet.BulletShooter;

import java.awt.Color;
import java.awt.Font;

public abstract class Enemy extends SpriteEntity implements BulletShooter {
    protected final EnemyConfig config;

    protected EnemyState state;

    private int index;
    private float indexTimer;
    private boolean action;

    private final GalagaSound dieSoundType;
    private Sound dieSound;
    private Font debugFont;

    public Enemy(EnemyConfig config, GalagaSound dieSound) {
        super();
        this.config = config;
        this.dieSoundType = dieSound;

        this.angle = 0.f;
        this.scale = Config.SPRITE_SCALE_DEFAULT;

        boolean isLeft = config.getLockPosition().getX() < Config.WINDOW_WIDTH / 2.f;
        this.position = isLeft ? Config.POSITION_ENEMY_LEFT.copy() : Config.POSITION_ENEMY_RIGHT.copy();

        this.state = EnemyState.ENTER_LEVEL;

        this.action = false;

        float distance = this.config.getLockPosition().distance(Position.of(
            Config.WINDOW_WIDTH / 2.f,
            0.f
        ));
        this.index = Math.round(distance/100.f);

        if(Application.DEBUG_MODE) {
            distance = 0;
        }
        this.indexTimer = distance * Config.DELAY_ENEMY_ENTER;
    }

    public final EnemyState getState() {
        return this.state;
    }

    public final EnemyType getType() {
        return this.config.getType();
    }

    public final int getScoreValue() {
        return this.config.getScoreValue();
    }

    public final Position getLockPosition() {
        return this.config.getLockPosition();
    }

    protected final boolean isInLockPosition() {
        float distance = this.position.distance(this.config.getLockPosition());
        return distance <= Config.POSITION_LOCK_THRESHOLD * 10;
    }

    protected final void animateToLockPosition(float dt) {
        float distance = this.position.distance(this.config.getLockPosition());
        float scaledSpeed = this.config.getLevel().getFormationSpeed() * dt + distance * dt;

        this.position.moveTo(this.config.getLockPosition(), scaledSpeed);
        this.angle = this.config.getLockPosition().angleTo(this.position) + 90.f;
        if (this.isInLockPosition()) {
            this.position = this.config.getLockPosition().copy();
        }
    }

    protected final void animateInLockPosition(float dt) {
        float amplitude = 10.f;
        float frequency = 2.f;
        float offsetX = (float) Math.sin(System.currentTimeMillis() * 0.001 * frequency + this.index * Math.PI / 4) * amplitude;
        this.position.setX(this.config.getLockPosition().getX() + offsetX);
    }

    public final boolean hasDoneAction() {
        return this.action;
    }

    public final void resetAction() {
        this.action = false;
        this.indexTimer = Config.DELAY_ENEMY_FORMATION;
    }

    @Override
    public Position getBulletSpawnPosition(Size bulletSize) {
        return this.getCenter().copy().add(Position.of(
                this.getScaledSize().getWidth() / 2 - bulletSize.getWidth() / 2,
                0));
    }

    @Override
    public float getBulletSpawnAngle() {
        return this.angle;
    }

    @Override
    public void onBulletHitSelf() {
        this.dieSound.play();
        Galaga.getContext().getState().particles.createExplosion(this);
    }

    @Override
    public void onBulletHitOther(BulletShooter player) {
        player.onBulletHitSelf();
    }

    @Override
    public boolean isBulletColliding(Bullet bullet) {
        return Collision.aabb(bullet.getPosition(), bullet.getSize(), this.getCenter(), this.getScaledSize());
    }

    public void onCollideWithPlayer() {
        Galaga.getContext().getState().particles.createExplosion(this);
    }

    public boolean canRemove() {
        return false;
    }

    public abstract boolean canPerformAction();

    protected abstract void updateAction(float dt);

    @Override
    public boolean init() {
        this.sprite = Galaga.getContext().getResource().get(this.config.getType());
        if (this.sprite == null) {
            return false;
        }
        this.size = this.sprite.getSize();

        this.dieSound = Galaga.getContext().getResource().get(this.dieSoundType);
        if (this.dieSound == null) {
            return false;
        }
        this.dieSound.setCapacity(Config.SIZE_ENEMY_DIE_CAPACITY);

        this.debugFont = Galaga.getContext().getResource().get(Config.FONTS, Config.VARIANT_FONT_TEXT);
        return true;
    }

    @Override
    public final void update(float dt) {

        switch (this.state) {
            case ENTER_LEVEL -> {
                this.indexTimer -= dt;
                if (this.indexTimer <= 0 && !this.action) {
                    this.action = true;
                    this.state = EnemyState.RETURNING;
                }
            }
            case RETURNING -> {
                if (Application.DEBUG_MODE) {
                    this.position = this.config.getLockPosition().copy();
                    this.state = EnemyState.FORMATION;
                }
                this.animateToLockPosition(dt);
                if (this.isInLockPosition()) {
                    this.state = EnemyState.FORMATION;
                }
            }
            case FORMATION -> {
                if (this.angle != 0.f) {
                    float dtAngle = Config.SPEED_ANGLE_ANIMATION * dt;
                    if (Math.abs(this.angle) <= dtAngle) {
                        this.angle = 0.f;
                    } else {
                        this.angle += (this.angle > 0.f ? -dtAngle : dtAngle);
                    }
                }

                this.animateInLockPosition(dt);

                this.indexTimer -= dt;
                if (this.indexTimer <= 0 && !this.action && this.canPerformAction()) {
                    this.action = true;
                    this.state = EnemyState.ATTACKING;
                }
            }
            case ATTACKING -> {
                this.updateAction(dt);
            }
        }
    }

    @Override
    public final void draw() {
        super.draw();

        if (Application.DEBUG_MODE) {
            String debugText = this.state.name();
            Application.getContext().getRenderer().drawText(debugText, this.getCenter(), Color.WHITE, this.debugFont);
        }
    }
}
