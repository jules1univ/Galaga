package game.entities.enemies;

import engine.AppContext;

public class EnemyButterFly extends Enemy {

    public EnemyButterFly(AppContext ctx, float startX, float startY, float size, int value, float speed) {
        super(ctx, EnemyType.BUTTERFLY, startX, startY, size, value, speed);
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
