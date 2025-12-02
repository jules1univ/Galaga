package game.entities.enemies;

import engine.utils.Position;

public class EnemyMoth extends Enemy {

    public EnemyMoth(boolean leftAnimation, Position lock, int value, float speed) {
        super(EnemyType.MOTH, leftAnimation, lock, value, speed);
        this.angle = 0;
    }

    @Override
    public void update(double dt) {
    }

}
