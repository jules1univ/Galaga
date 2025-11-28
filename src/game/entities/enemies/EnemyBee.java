package game.entities.enemies;

public class EnemyBee extends Enemy {

    public EnemyBee(float lockX, float lockY, int value, float speed) {
        super(EnemyType.BEE, lockX, lockY, value, speed);
    }

    @Override
    public boolean init() {
        super.init();
        return true;
    }

}
