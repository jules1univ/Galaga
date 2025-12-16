package galaga.entities.enemies.types;

import engine.utils.Position;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaSound;
import galaga.entities.enemies.Enemy;
import galaga.entities.enemies.EnemyConfig;
import galaga.entities.enemies.EnemyState;
import galaga.entities.enemies.EnemyType;

public class EnemyButterFly extends Enemy {

    private final Position target;
    private float timer = 0.f;
  
    public EnemyButterFly(EnemyConfig config) {
        super(config, GalagaSound.enemy_medium_die);
        assert config.getType() == EnemyType.BUTTERFLY;

        this.target = this.config.getLockPosition().copy().setY(Config.WINDOW_HEIGHT - Config.HEIGHT_FUD * 2.f);
        this.timer = config.getLevel().getMissileCooldown();
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

        float distance = this.position.distance(target);
        float scaledSpeed = this.config.getSpeed() * dt + distance * dt;

        this.position.moveTo(target, scaledSpeed);
        this.angle = 180.f;


        if (this.position.distance(target) <= Config.POSITION_NEAR_THRESHOLD) {
            this.state = EnemyState.RETURNING;
        }

        this.timer += dt;
        if (this.timer >= this.config.getLevel().getMissileCooldown()) {
            this.timer = 0.f;
            Galaga.getContext().getState().bullets.shoot(this);
        }
    }

}
