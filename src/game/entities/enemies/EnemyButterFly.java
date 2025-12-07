package game.entities.enemies;

import engine.utils.Position;

public class EnemyButterFly extends Enemy {

    public EnemyButterFly(Position lock, int value, float speed, float formationSpeed, int missileCooldown) {
        super(EnemyType.BUTTERFLY, lock, value, speed, formationSpeed);
    }

    @Override
    protected void updateAction(double dt) {
    }

}
