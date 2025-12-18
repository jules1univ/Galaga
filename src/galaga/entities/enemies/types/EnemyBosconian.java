package galaga.entities.enemies.types;

import engine.utils.Position;
import engine.utils.Size;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaSound;
import galaga.entities.enemies.Enemy;
import galaga.entities.enemies.EnemyConfig;
import galaga.entities.enemies.EnemyState;
import galaga.entities.enemies.EnemyType;

public class EnemyBosconian extends Enemy {

    private Position target;
    private float timer;
    private boolean shootRight;

    public EnemyBosconian(EnemyConfig config) {
        super(config, GalagaSound.enemy_boss_die);

        assert config.getType() == EnemyType.BOSCONIAN;
    }

    @Override
    public Position getBulletSpawnPosition(Size bulletSize) {
        this.shootRight = !this.shootRight;
        return this.getCenter().copy().add(Position.of(
                this.shootRight ? (this.getScaledSize().getWidth() - bulletSize.getWidth() / 2.f)
                        : bulletSize.getWidth() / 2.f,
                0));
    }

    @Override
    public boolean canPerformAction() {
        return this.config.getLevel().getMissileCooldown() > 0.f;
    }

    @Override
    protected void updateAction(float dt) {
        if (this.state != EnemyState.ATTACKING) {
            return;
        }


        if (this.target == null) {
            this.target = Position.of(
                    this.getLockPosition().getX() < Config.WINDOW_WIDTH / 2 ? Config.POSTION_BEE_RIGHT
                            : Config.POSTION_BEE_LEFT,
                    this.getLockPosition().getY());
        }

        float distance = this.position.distance(this.target);
        float scaledSpeed = this.config.getSpeed() * dt + distance * dt;

        this.position.moveTo(this.target, scaledSpeed);
        this.angle = 180.f;

        if (this.position.distance(this.target) <= Config.POSITION_NEAR_THRESHOLD) {

            if (this.target.getY() >= Config.POSITION_BEE_BOTTOM) {
                this.state = EnemyState.RETURNING;
            } else {
                this.target.addY(Config.POSITION_BEE_STEP_Y);
                this.target.setX(this.target.getX() == Config.POSTION_BEE_LEFT ? Config.POSTION_BEE_RIGHT : Config.POSTION_BEE_LEFT);
            }
        }

        this.timer += dt;
        if (this.timer >= this.config.getLevel().getMissileCooldown() * Config.DELAY_ENEMY_BOSCONIAN_SHOOT_FACTOR) {
            this.timer = 0.f;

            Galaga.getContext().getState().bullets.shoot(this);
            Galaga.getContext().getState().bullets.shoot(this);

        }
    }

}
