package galaga.entities.enemies.types;

import engine.utils.Position;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaSound;
import galaga.entities.enemies.Enemy;
import galaga.entities.enemies.EnemyConfig;
import galaga.entities.enemies.EnemyType;

public class EnemyCapturedPlayer extends Enemy {
    private float timer;

    public EnemyCapturedPlayer(EnemyConfig config) {
        super(config, GalagaSound.enemy_medium_die);
        assert config.getType() == EnemyType.CAPTURED_PLAYER;
    }

    @Override
    public boolean canPerformAction() {
        return config.getLevel().getMissileCooldown() > 0.f;
    }

    @Override
    public boolean canRemove() {
        return  this.position.distance(Galaga.getContext().getState().player.getPosition()) <= Config.POSITION_NEAR_CAPTURED_PLAYER_THRESHOLD;
    }

    @Override
    protected void updateAction(float dt) {
        Position target = Galaga.getContext().getState().player.getCenter().copy();

        float distance = this.position.distance(target);
        float scaledSpeed = this.config.getSpeed() * .5f *  dt + distance * dt;

        this.position.moveTo(target, scaledSpeed);
        this.angle = this.position.angleTo(target) - 90.f;

        this.timer +=  dt;
        if (this.timer >= this.config.getLevel().getMissileCooldown() * Config.DELAY_ENEMY_CAPTURED_PLAYER_SHOOT_FACTOR) {
            this.timer = 0.f;
            Galaga.getContext().getState().bullets.shoot(this);
        }
    }

}
