package game.entities.enemies;

import engine.utils.Position;

public class EnemyButterFly extends Enemy {

    public EnemyButterFly(Position position, int value, float speed) {
        super(EnemyType.BUTTERFLY, position, value, speed);
    }

    

    @Override
    public void update(double dt) {
    }

}
