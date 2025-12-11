package galaga.entities.bullet;

import engine.elements.entity.Entity;
import engine.elements.entity.SpriteEntity;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BulletManager extends Entity implements  Iterable<Bullet> {

    private final List<Bullet> bullets = new ArrayList<>();

    public BulletManager() {
    }

    public void shoot(SpriteEntity shooter) {
        this.bullets.add(new Bullet(shooter));
    }

    
    @Override
    public Iterator<Bullet> iterator() {
        return bullets.iterator();
    }

    public void removeAll(List<Bullet> removeBullet) {
        this.bullets.removeAll(removeBullet);
    }

    public boolean isEmpty() {
        return this.bullets.isEmpty();
    }

    @Override
    public boolean init() {
        throw new UnsupportedOperationException("Do not call init on BulletManager");
    }

    @Override
    public void update(float dt) {
        throw new UnsupportedOperationException("Do not call update on BulletManager");
    }

    @Override
    public void draw() {
        for (Bullet bullet : bullets) {
            bullet.draw();
        }
    }


}
