package galaga.entities.enemies;

import engine.Application;
import engine.elements.entity.SpriteEntity;
import engine.graphics.Renderer;
import engine.resource.sound.Sound;
import engine.utils.Collision;
import engine.utils.Position;
import engine.utils.Size;
import galaga.Config;
import galaga.Galaga;
import galaga.entities.bullet.Bullet;
import galaga.entities.bullet.BulletShooter;
import galaga.resources.sound.GalagaSound;

import java.awt.Color;
import java.awt.Font;

public abstract class Enemy extends SpriteEntity implements BulletShooter {
    protected final EnemyConfig config;

    protected EnemyState state;

    private int index;
    private float indexTimer;
    private boolean action;

    private float bezierTime;

    private boolean enterLeft;
    private boolean enterMidPassed;

    private Position enterPosition;
    private Position midPosition;

    private final GalagaSound dieSoundType;
    private Sound dieSound;
    private Font debugFont;

    public Enemy(EnemyConfig config, GalagaSound dieSound) {
        super();
        this.config = config;
        this.dieSoundType = dieSound;

        this.angle = 0.f;
        this.scale = Config.SPRITE_SCALE_DEFAULT;

        this.enterLeft = config.getLockPosition().getX() < Config.WINDOW_WIDTH / 2.f;
        this.enterMidPassed = false;

        this.enterPosition = this.enterLeft ? Config.POSITION_ENEMY_LEFT.copy() : Config.POSITION_ENEMY_RIGHT.copy();
        this.midPosition = this.enterLeft ? Config.POSITION_ENEMY_MID_LEFT.copy()
                : Config.POSITION_ENEMY_MID_RIGHT.copy();
        this.position = this.enterPosition.copy();

        this.state = EnemyState.ENTER_LEVEL;

        this.action = false;

        this.index = this.config.getIndex();
        this.indexTimer = this.index * Config.DELAY_ENEMY_ENTER;
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

    private void animateCubicBezier(Position start, Position control1, Position control2, Position end, float move) {
        this.bezierTime += move;
        this.bezierTime = Math.min(this.bezierTime, 1f);

        float u = 1 - this.bezierTime;

        float tt = this.bezierTime * this.bezierTime;
        float ttt = tt * this.bezierTime;

        float uu = u * u;
        float uuu = uu * u;

        float dx = 3 * uu * (control1.getX() - start.getX()) +
                6 * u * this.bezierTime * (control2.getX() - control1.getX()) +
                3 * tt * (end.getX() - control2.getX());

        float dy = 3 * uu * (control1.getY() - start.getY()) +
                6 * u * this.bezierTime * (control2.getY() - control1.getY()) +
                3 * tt * (end.getY() - control2.getY());

        this.angle = (float) Math.toDegrees(Math.atan2(dy, dx)) + 90.f;

        this.position.setX(uuu * start.getX() +
                3 * uu * this.bezierTime * control1.getX() +
                3 * u * tt * control2.getX() +
                ttt * end.getX());

        this.position.setY(uuu * start.getY() +
                3 * uu * this.bezierTime * control1.getY() +
                3 * u * tt * control2.getY() +
                ttt * end.getY());

    }

    private void animateEnterToMidPoint(float dt) {
        if (this.enterMidPassed) {
            return;
        }

        this.animateCubicBezier(this.enterPosition,
                this.enterLeft ? Config.POSITION_ENTER_MID_LEFT_CTRL : Config.POSITION_ENTER_MID_RIGHT_CTRL,
                this.enterLeft ? Config.POSITION_ENTER_MID_LEFT_CTRL_2 : Config.POSITION_ENTER_MID_RIGHT_CTRL_2,
                this.midPosition,
                dt * Config.SPEED_ENEMY_ANIMATION_ENTER / 2.f);

        float distance = this.position.distance(this.midPosition);
        if (distance <= Config.POSITION_LOCK_THRESHOLD * 10) {
            this.position = this.midPosition.copy();
            this.bezierTime = 0.f;
            this.enterMidPassed = true;
        }
    }

    private void animateEnterToLockPosition(float dt) {
        if (!this.enterMidPassed) {
            this.animateEnterToMidPoint(dt);
            return;
        }

        this.animateCubicBezier(this.midPosition, Config.POSITION_ENTER_LOCK_CTRL, Config.POSITION_ENTER_LOCK_CTRL_2,
                this.config.getLockPosition(),
                dt * Config.SPEED_ENEMY_ANIMATION_ENTER);
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
        float offsetX = (float) Math.sin(System.currentTimeMillis() * 0.001 * frequency + this.index * Math.PI / 4)
                * amplitude;
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
                if (this.indexTimer <= 0.f && !this.action) {
                    this.animateEnterToLockPosition(dt);
                    if (this.isInLockPosition()) {
                        this.action = true;
                        this.state = EnemyState.RETURNING;
                    }
                }
            }
            case RETURNING -> {
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
    public final void draw(Renderer renderer) {
        super.draw(renderer);

        if (Application.DEBUG_MODE) {

            if (!this.enterMidPassed) {

                renderer.drawCubicBezier(
                        this.enterLeft ? Config.POSITION_ENEMY_LEFT : Config.POSITION_ENEMY_RIGHT,
                        this.enterLeft ? Config.POSITION_ENTER_MID_LEFT_CTRL : Config.POSITION_ENTER_MID_RIGHT_CTRL,
                        this.enterLeft ? Config.POSITION_ENTER_MID_LEFT_CTRL_2 : Config.POSITION_ENTER_MID_RIGHT_CTRL_2,
                        this.midPosition, Color.WHITE);
            } else {
                renderer.drawCubicBezier(
                        this.position,
                        Config.POSITION_ENTER_LOCK_CTRL,
                        Config.POSITION_ENTER_LOCK_CTRL_2,
                        this.config.getLockPosition(), Color.RED);
            }

            String debugText = this.state.name();
            renderer.drawText(debugText, this.getCenter(), Color.WHITE, this.debugFont);
        }
    }
}
