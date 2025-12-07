package game.entities.bullet;

import engine.elements.entity.Entity;
import engine.elements.entity.SpriteEntity;
import game.Config;

public class Bullet extends Entity {

    private final SpriteEntity shooter;

    public Bullet(SpriteEntity shooter) {
        super();
        this.shooter = shooter;
        this.position = shooter.getPosition().copy();
        this.angle = shooter.getAngle();
    }


    @Override
    public boolean init() {
        return true;
    }

    @Override
    public void update(double dt) {
        this.position.moveInDirection(this.angle, Config.SPEED_BULLET * (float) dt);
    }

    @Override
    public void draw() {
    }
    
}
