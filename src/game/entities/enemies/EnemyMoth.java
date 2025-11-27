package game.entities.enemies;

public class EnemyMoth extends Enemy {

    public EnemyMoth(float endX, float endY, float size, int value, float speed) {
        super(EnemyType.MOTH, endX, endY, size, value, speed);
    }

    @Override
    public boolean init() {
        super.init();
        return true;
    }

}
