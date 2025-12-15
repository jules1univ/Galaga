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

    protected final EnemyType type;
    protected final Position lock;

    protected final float speed;
    protected final float formationSpeed;

    protected final int scoreValue;
    protected EnemyState state;

    private float indexTimer;
    private boolean action;

    private Sound dieSound;
    private Font debugFont;

    public Enemy(EnemyType type, EnemySetting setting, float formationSpeed) {
        super();
        this.type = type;

        this.angle = 0.f;
        this.scale = Config.SPRITE_SCALE_DEFAULT;

        boolean isLeft = setting.getLockPosition().getX() < Config.WINDOW_WIDTH / 2.f;
        this.position = isLeft ? Config.POSITION_ENEMY_LEFT.copy() : Config.POSITION_ENEMY_RIGHT.copy();
        this.lock = setting.getLockPosition().copy();

        this.speed = setting.getSpeed();
        this.formationSpeed = formationSpeed;
        this.scoreValue = setting.getScoreValue();

        this.state = EnemyState.ENTER_LEVEL;

        this.action = false;
        this.indexTimer = setting.getEnterIndex() * Config.DELAY_ENEMY_ENTER;
    }

    public EnemyState getState() {
        return this.state;
    }

    public EnemyType getType() {
        return this.type;
    }

    public int getScoreValue() {
        return this.scoreValue;
    }

    public Position getLockPosition() {
        return this.lock;
    }

    protected boolean isInLockPosition() {
        float distance = this.position.distance(this.lock);
        return distance <= Config.POSITION_LOCK_THRESHOLD * 10;
    }

    protected void animateToLockPosition(float dt) {
        float distance = this.position.distance(this.lock);
        float scaledSpeed = this.formationSpeed * (float) dt + distance * (float) dt;

        this.position.moveTo(this.lock, scaledSpeed);
        this.angle = this.lock.angleTo(this.position) + 90.f;
        if (this.isInLockPosition()) {
            this.position = this.lock.copy();
        }
    }

    public boolean hasDoneAction() {
        return this.action;
    }

    public void resetAction() {
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
        return Collision.aabb(bullet.getPosition(), bullet.getSize(), this.getPosition(), this.getScaledSize());
    }

    public void onCollideWithPlayer() {
        Galaga.getContext().getState().particles.createExplosion(this);
    }

    public abstract boolean canPerformAction();

    protected abstract void updateAction(float dt);

    @Override
    public final boolean init() {
        this.sprite = Galaga.getContext().getResource().get(this.type);
        if (this.sprite == null) {
            return false;
        }
        this.size = this.sprite.getSize();

        GalagaSound sound;
        switch (this.type) {
            case BEE -> sound = GalagaSound.enemy_bee_die;
            case BUTTERFLY -> sound = GalagaSound.enemy_butterfly_die;
            case MOTH -> sound = GalagaSound.enemy_moth_die;
            default -> {
                return false;
            }
        }
        this.dieSound = Galaga.getContext().getResource().get(sound);
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
                this.indexTimer -= (float) dt;
                if (this.indexTimer <= 0 && !this.action) {
                    this.action = true;
                    this.state = EnemyState.RETURNING;
                }
            }
            case RETURNING -> {
                if (Application.DEBUG_MODE) {
                    this.position = this.lock.copy();
                    this.state = EnemyState.FORMATION;
                }
                this.animateToLockPosition(dt);
                if (this.isInLockPosition()) {
                    this.state = EnemyState.FORMATION;
                }
            }
            case FORMATION -> {
                if (this.angle != 0.f) {
                    float dtAngle = Config.SPEED_ANGLE_ANIMATION * (float) dt;
                    if (Math.abs(this.angle) <= dtAngle) {
                        this.angle = 0.f;
                    } else {
                        this.angle += (this.angle > 0.f ? -dtAngle : dtAngle);
                    }
                }

                this.indexTimer -= (float) dt;
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
