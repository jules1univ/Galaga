package galaga.entities.bullet;

import engine.elements.entity.Entity;
import engine.graphics.Renderer;
import engine.resource.sound.Sound;
import galaga.Config;
import galaga.Galaga;
import galaga.resources.sound.GalagaSound;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BulletManager extends Entity implements  Iterable<Bullet> {
    
    private final List<Bullet> bullets = new ArrayList<>();
    private Sound shootSound;

    public BulletManager() {
        super();
    }

    public void shoot(BulletShooter shooter) {
        if(this.bullets.size() >= Config.SIZE_BULLET_CAPACITY) {
            for(Bullet bullet : this.bullets) {
                if(bullet.isOutOfBounds()) {
                    this.bullets.remove(bullet);
                    break;
                }
            }
        }
        this.shootSound.play();
        this.bullets.add(new Bullet(shooter));
    }

    
    @Override
    public Iterator<Bullet> iterator() {
        return this.bullets.iterator();
    }

    public boolean isEmpty() {
        return this.bullets.isEmpty();
    }

    @Override
    public boolean init() {
        this.shootSound = Galaga.getContext().getResource().get(GalagaSound.entity_shoot);
        if(this.shootSound == null) {
            return false;
        }
        this.shootSound.setCapacity(Config.SIZE_BULLET_CAPACITY);
        return true;
    }

    @Override
    public void update(float dt) {
        throw new UnsupportedOperationException("BulletManager.update should not be called");
    }

    @Override
    public void draw(Renderer renderer) {
        for(Bullet bullet : this.bullets) {
            bullet.draw(renderer);
        }
    }


}
