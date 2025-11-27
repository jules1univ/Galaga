package game.entities.enemies;

import engine.AppContext;

public class EnemyMoth extends Enemy {

    public EnemyMoth(AppContext ctx, float startX, float startY, float size, int value, float speed) {
        super(ctx, EnemyType.MOTH, startX, startY, size, value, speed);
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
