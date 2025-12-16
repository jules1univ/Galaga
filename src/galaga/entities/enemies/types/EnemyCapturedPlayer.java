package galaga.entities.enemies.types;

import galaga.GalagaSound;
import galaga.entities.enemies.Enemy;
import galaga.entities.enemies.EnemyConfig;
import galaga.entities.enemies.EnemyType;

public class EnemyCapturedPlayer extends Enemy {


    public EnemyCapturedPlayer(EnemyConfig config) {
        super(config, GalagaSound.enemy_medium_die);
        assert config.getType() == EnemyType.CAPTURED_PLAYER;
    }

    @Override
    public boolean canPerformAction() {
        return config.getLevel().getMissileCooldown() > 0.f;
    }

    @Override
    protected void updateAction(float dt) {
    }

}
