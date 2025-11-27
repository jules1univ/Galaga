package game.entities.enemies;

public class EnemyBee extends Enemy {

    public EnemyBee(float startX, float startY, float size, int value, float speed) {
        super(EnemyType.BEE, startX, startY, size, value, speed);
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
