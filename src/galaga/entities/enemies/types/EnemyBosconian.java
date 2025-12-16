package galaga.entities.enemies.types;

import galaga.GalagaSound;
import galaga.entities.enemies.Enemy;
import galaga.entities.enemies.EnemyConfig;
import galaga.entities.enemies.EnemyState;
import galaga.entities.enemies.EnemyType;

public class EnemyBosconian extends Enemy {

    public EnemyBosconian(EnemyConfig config) {
        super(config, GalagaSound.enemy_boss_die);

        assert config.getType() == EnemyType.BOSCONIAN;
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

    }

}
