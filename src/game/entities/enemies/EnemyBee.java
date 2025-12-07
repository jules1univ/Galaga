package game.entities.enemies;

import engine.utils.Position;

public class EnemyBee extends Enemy {

    private final int missileCooldown;

    public EnemyBee(Position lock, int value, float speed, float formationSpeed, int missileCooldown) {
        super(EnemyType.BEE, lock, value, speed, formationSpeed);
        this.missileCooldown = missileCooldown;
    }

    @Override
    protected void updateAction(double dt) {
    }


}
