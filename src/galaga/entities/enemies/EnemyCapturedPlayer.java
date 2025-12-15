package galaga.entities.enemies;

public class EnemyCapturedPlayer extends Enemy {


    public EnemyCapturedPlayer(EnemySetting setting, float formationSpeed, float missileCooldown) {
        super(EnemyType.CAPTURED_PLAYER, setting, formationSpeed);
    }

    @Override
    public boolean canPerformAction() {
        return false;
    }

    @Override
    protected void updateAction(float dt) {
    }

}
