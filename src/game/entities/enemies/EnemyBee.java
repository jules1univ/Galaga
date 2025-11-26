package game.entities.enemies;

public class EnemyBee extends Enemy {

    public EnemyBee(engine.AppContext ctx) {
        super(ctx, EnemyType.BEE);
    }

    @Override
    public boolean init() {
        super.init();
        this.x = (this.ctx.frame.getWidth() - this.sprite.getWidth()) / 2;
        this.y = 50;
        return true;
    }

    @Override
    public void update(double dt) {

    }

}
