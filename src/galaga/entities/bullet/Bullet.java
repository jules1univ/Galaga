package galaga.entities.bullet;

import engine.elements.entity.Entity;
import engine.utils.Collision;
import engine.utils.Position;
import engine.utils.Size;
import galaga.Config;
import galaga.Galaga;

public class Bullet extends Entity {

    private final float angle;
    private final BulletShooter shooter;
    private final Size boxSize;

    public Bullet(BulletShooter shooter) {
        super();
        this.angle = shooter.getBulletSpawnAngle() - 90;
        this.boxSize = Size.of(2, 20);
        this.size = Size.of(10, 20);

        this.shooter = shooter;
        this.position = shooter.getBulletSpawnPosition(this.boxSize);
    }

    public BulletShooter getShooter() {
        return this.shooter;
    }

    
    public boolean isOutOfBounds() {
        return Collision.aabb(this.position, this.boxSize,
                Position.zero(),
                Size.of(Galaga.getContext().getFrame().getWidth(), Galaga.getContext().getFrame().getHeight())) == false;
    }

    @Override
    public boolean init() {
        throw new UnsupportedOperationException("Bullet.init should not be called");
    }

    @Override
    public void update(float dt) {
        this.position.moveTo(this.angle, (float) dt * Config.SPEED_BULLET);
    }

    @Override
    public void draw() {
        Galaga.getContext().getRenderer().drawRect(this.position, this.boxSize, Config.COLOR_BULLET, this.angle + 90.f);
        super.draw();
    }

 
}
