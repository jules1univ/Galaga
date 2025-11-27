package game.entities.enemies;

import engine.AppContext;

public class EnemyBee extends Enemy {

    public EnemyBee(AppContext ctx, float startX, float startY, float size, int value, float speed) {
        super(ctx, EnemyType.BEE, startX, startY, size, value, speed);
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
