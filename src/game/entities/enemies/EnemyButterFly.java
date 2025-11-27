package game.entities.enemies;

public class EnemyButterFly extends Enemy {

    public EnemyButterFly(float endX, float endY, float size, int value, float speed) {
        super(EnemyType.BUTTERFLY, endX, endY, size, value, speed);
    }

    @Override
    public boolean init() {
        super.init();
        return true;
    }

}
