package game.entities.bullet;

import engine.elements.entity.Entity;
import engine.elements.entity.SpriteEntity;
import java.util.ArrayList;
import java.util.List;

public class BulletManager extends Entity {
    private final List<Bullet> bullets = new ArrayList<>();
    

    public BulletManager() {
    }

    public void shoot(SpriteEntity shooter) {
        this.bullets.add(new Bullet(shooter));
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public void update(double dt) {
        List<Bullet> toRemove = new ArrayList<>();
        for (Bullet bullet : bullets) {
            bullet.update(dt);

            if (!bullet.isOutOfBounds()) {
                toRemove.add(bullet);
            }
        }
        bullets.removeAll(toRemove);
    }

    @Override
    public void draw() {
        for (Bullet bullet : bullets) {
            bullet.draw();
        }
    }

}
