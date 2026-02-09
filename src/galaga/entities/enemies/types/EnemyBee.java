package galaga.entities.enemies.types;

import engine.utils.Position;
import galaga.Config;
import galaga.Galaga;
import galaga.entities.enemies.Enemy;
import galaga.entities.enemies.EnemyConfig;
import galaga.entities.enemies.EnemyState;
import galaga.entities.enemies.EnemyType;
import galaga.resources.sound.GalagaSound;

public class EnemyBee extends Enemy {

    private float timer;
    private Position target;

    public EnemyBee(EnemyConfig config) {
        super(config, GalagaSound.enemy_small_die);

        assert config.getType() == EnemyType.BEE;
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

        if(this.target == null)
        {
            this.target = Position.of(
                this.getLockPosition().getX() < Config.WINDOW_WIDTH / 2 ? Config.POSTION_BEE_RIGHT : Config.POSTION_BEE_LEFT,
                350.f
            );
        }

        float distance = this.position.distance(this.target);
        float scaledSpeed = this.config.getSpeed() * dt + distance * dt;

        this.position.moveTo(this.target, scaledSpeed);
        this.angle = 180.f;

        if (this.position.distance(this.target) <= Config.POSITION_NEAR_THRESHOLD) {

            if(this.target.getY() >= Config.POSITION_BEE_BOTTOM)
            {
                this.state = EnemyState.RETURNING;
            }else{
                this.target.addY(Config.POSITION_BEE_STEP_Y);
                this.target.setX(this.target.getX() == Config.POSTION_BEE_LEFT ? Config.POSTION_BEE_RIGHT : Config.POSTION_BEE_LEFT);
            }
        }

        this.timer += dt;
        if (this.timer >= this.config.getLevel().getMissileCooldown()) {
            this.timer = 0.f;
            Galaga.getContext().getState().bullets.shoot(this);
        }
    }

}
