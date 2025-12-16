package galaga.entities.enemies.types;

import engine.utils.Position;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaSound;
import galaga.entities.enemies.Enemy;
import galaga.entities.enemies.EnemyConfig;
import galaga.entities.enemies.EnemyState;
import galaga.entities.enemies.EnemyType;

public class EnemyBee extends Enemy {

    private int zigZagIndex = 0;
    private float timer;

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

        Position target = Config.POSITION_ZIG_ZAG.get(this.zigZagIndex);
        float distance = this.position.distance(target);
        float scaledSpeed = this.config.getSpeed() * dt + distance * dt;

        this.position.moveTo(target, scaledSpeed);
        this.angle = 180.f;

        if (this.position.distance(target) <= Config.POSITION_NEAR_THRESHOLD) {
            this.zigZagIndex++;
            if (this.zigZagIndex >= Config.POSITION_ZIG_ZAG.size()) {
                this.zigZagIndex = 0;
                this.state = EnemyState.RETURNING;
            }
        }

        this.timer += dt;
        if (this.timer >= this.config.getLevel().getMissileCooldown()) {
            this.timer = 0.f;
            Galaga.getContext().getState().bullets.shoot(this);
        }
    }

}
