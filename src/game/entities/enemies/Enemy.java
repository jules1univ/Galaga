package game.entities.enemies;

import engine.Application;
import engine.elements.entity.SpriteEntity;
import engine.utils.Position;
import game.Config;
import game.Galaga;
import java.awt.Color;
import java.awt.Font;

public abstract class Enemy extends SpriteEntity {

    protected final float speed;
    protected final float formationSpeed;
    protected final int scoreValue;
    protected final EnemyType type;
    protected final Position lock;

    protected int index;
    private float indexTimer;
    protected EnemyState state;

    private Font debugFont;

    public Enemy(EnemyType type, Position lock, int value, float speed, float formationSpeed) {
        super();
        this.type = type;

        this.angle = 0.f;
        this.scale = Config.SPRITE_SCALE_DEFAULT;

        boolean isLeft = lock.getX() < Config.WINDOW_WIDTH / 2.f;
        this.position = isLeft ? Config.POSITION_ENEMY_LEFT.copy() : Config.POSITION_ENEMY_RIGHT.copy();
        this.lock = lock.copy();

        this.speed = speed;
        this.formationSpeed = formationSpeed;
        this.scoreValue = value;

        this.index = Config.POSITION_ENEMY_INDEX_NOTSET;
        this.state = EnemyState.ENTER_LEVEL;
    }

    public EnemyType getType() {
        return type;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    private boolean isInLockPosition() {
        float distance = this.position.distance(this.lock);
        return distance <= Config.POSITION_LOCK_THRESHOLD * 10;
    }

    private void animateToLockPosition(double dt) {
        float distance = this.position.distance(this.lock);
        float scaledSpeed = this.formationSpeed * (float) dt + distance * (float) dt;

        this.position.moveTo(this.lock, scaledSpeed);
        this.angle = this.lock.angleTo(this.position) + 90.f;
        if (this.isInLockPosition()) {
            this.angle = 0.f;
            this.position = this.lock.copy();
        }
    }

    protected abstract void updateAction(double dt);

    @Override
    public final boolean init() {
        this.sprite = Galaga.getContext().getResource().get(this.type);
        if (this.sprite == null) {
            return false;
        }
        this.size = this.sprite.getSize();
        this.debugFont = Galaga.getContext().getResource().get(Config.FONTS, Config.VARIANT_FONT_TEXT);
        return true;
    }

    @Override
    public final void update(double dt) {

        switch (this.state) {
            case ENTER_LEVEL -> {
                if (this.index == Config.POSITION_ENEMY_INDEX_NOTSET) {
                    break;
                }
                if (this.indexTimer < this.index * Config.DELAY_ENEMY_ENTER) {
                    this.indexTimer += (float) dt;
                } else {
                    this.indexTimer = 0;
                    this.state = EnemyState.RETURNING;
                }
            }
            case RETURNING -> {
                this.animateToLockPosition(dt);
                if (this.isInLockPosition()) {
                    this.state = EnemyState.FORMATION;
                }
            }
            case FORMATION -> {
                if (this.index == Config.POSITION_ENEMY_INDEX_NOTSET) {
                    break;
                }

                if (this.indexTimer < this.index * Config.DELAY_ENEMY_ROUND) {
                    this.indexTimer += (float) dt;
                } else {
                    this.indexTimer = 0;
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

            float delayPercent = Math.clamp(((this.indexTimer / Config.DELAY_ENEMY_ROUND)), 0.f, 1.f) * 100.f;
            String debugText = String.format("%.2f%%", delayPercent);
            Application.getContext().getRenderer().drawText(debugText, this.getCenter(), Color.WHITE, this.debugFont);

        }
    }
}
