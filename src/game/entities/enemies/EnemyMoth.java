package game.entities.enemies;

public class EnemyMoth extends Enemy {

    public EnemyMoth(float startX, float startY, float size, int value, float speed) {
        super(EnemyType.MOTH, startX, startY, size, value, speed);
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
