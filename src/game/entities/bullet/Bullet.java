package game.entities.bullet;

import engine.elements.entity.Entity;
import engine.elements.entity.SpriteEntity;
import engine.utils.Position;
import engine.utils.Size;
import game.Config;
import game.Galaga;

public class Bullet extends Entity {

    private final float angle;
    private final SpriteEntity shooter;

    public Bullet(SpriteEntity shooter) {
        super();
        this.angle = shooter.getAngle() - 90.f;
        this.size = Size.of(2, 20);

        this.shooter = shooter;
        this.position = shooter.getPosition().copy().add(Position.of(this.size).negate().half());
    }

    public Entity getShooter() {
        return shooter;
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public void update(double dt) {
        this.position.moveTo(this.angle, (float) dt * Config.SPEED_BULLET);
    }

    @Override
    public void draw() {
        Galaga.getContext().getRenderer().drawRect(this.position, this.size, Config.COLOR_BULLET, this.angle + 90.f);
        super.draw();
    }
    
}
