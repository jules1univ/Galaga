package game.entities.enemies;

import engine.utils.Position;

public class EnemyMoth extends Enemy {

    public EnemyMoth(Position lock, int value, float speed, float formationSpeed, float attackCooldown) {
        super(EnemyType.MOTH, lock, value, speed, formationSpeed);
    }

    
    @Override
    protected void updateAction(double dt) {
    }


}
