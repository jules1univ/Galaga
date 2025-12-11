package galaga.entities.enemies;

import engine.Application;
import engine.elements.entity.SpriteEntity;
import engine.utils.Position;
import galaga.Config;
import galaga.Galaga;
import java.awt.Color;
import java.awt.Font;

public abstract class Enemy extends SpriteEntity {

    protected final EnemyType type;
    protected final Position lock;

    protected final float speed;
    protected final float formationSpeed;

    protected final int scoreValue;
    protected EnemyState state;

    private final int actionIndex;
    private float indexTimer;
    private boolean action;

    private Font debugFont;

    public Enemy(EnemyType type, Position lock, int actionIndex, int enterIndex, int value, float speed, float formationSpeed) {
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

        this.state = EnemyState.ENTER_LEVEL;

        this.action = false;
        this.actionIndex = actionIndex; 

        this.indexTimer = enterIndex * Config.DELAY_ENEMY_ENTER;
    }

    public boolean hasDoneAction() {
        return this.action;
    }

    public void resetAction() {
        this.action = false;
        this.indexTimer = this.actionIndex * Config.DELAY_ENEMY_FORMATION;
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

    private boolean isInLockPosition() {
        float distance = this.position.distance(this.lock);
        return distance <= Config.POSITION_LOCK_THRESHOLD * 10;
    }

    private void animateToLockPosition(float dt) {
        float distance = this.position.distance(this.lock);
        float scaledSpeed = this.formationSpeed * (float) dt + distance * (float) dt;

        this.position.moveTo(this.lock, scaledSpeed);
        this.angle = this.lock.angleTo(this.position) + 90.f;
        if (this.isInLockPosition()) {
            this.angle = 0.f;
            this.position = this.lock.copy();
        }
    }

    protected abstract void updateAction(float dt);

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
    public final void update(float dt) {

        switch (this.state) {
            case ENTER_LEVEL -> {
                this.indexTimer -= (float) dt;
                if (this.indexTimer <= 0 && !this.action) {
                    this.indexTimer = this.actionIndex * Config.DELAY_ENEMY_FORMATION;
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
                this.indexTimer -= (float) dt;
                if (this.indexTimer <= 0 && !this.action) {
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
            String debugText = String.format("%.2f%%", this.action ? this.indexTimer : 0.f);
            Application.getContext().getRenderer().drawText(debugText, this.getCenter(), Color.WHITE, this.debugFont);
        }
    }
}
