package game.entities.enemies;

import engine.utils.Position;

public class EnemyMoth extends Enemy {

    public EnemyMoth(Position position, int value, float speed) {
        super(EnemyType.MOTH, position, value, speed);
        this.angle = 0;
    }

    @Override
    public void update(double dt) {
    }

}
