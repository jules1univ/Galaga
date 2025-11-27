package game.entities.enemies;

public class EnemyButterFly extends Enemy {

    public EnemyButterFly(float startX, float startY, float size, int value, float speed) {
        super(EnemyType.BUTTERFLY, startX, startY, size, value, speed);
    }

    @Override
    public boolean init() {
        super.init();
        return true;
    }

    @Override
    public void update(double dt) {

    }

}
