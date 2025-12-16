package galaga.entities.enemies.types;

import engine.utils.Position;
import galaga.Galaga;
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
        Position target = Galaga.getContext().getState().player.getCenter().copy();

        float distance = this.position.distance(target);
        float scaledSpeed = this.config.getSpeed() * (float) dt + distance * (float) dt;

        this.position.moveTo(target, scaledSpeed);
        this.angle = this.position.angleTo(target) - 90.f;
    }

}
