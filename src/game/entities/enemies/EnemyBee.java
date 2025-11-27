package game.entities.enemies;

public class EnemyBee extends Enemy {

    public EnemyBee(float endX, float endY, float size, int value, float speed) {
        super(EnemyType.BEE, endX, endY, size, value, speed);
    }

    @Override
    public boolean init() {
        super.init();
        return true;
    }

}
