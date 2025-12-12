package galaga.entities.bullet;

import engine.elements.entity.Entity;
import engine.elements.entity.SpriteEntity;
import engine.resource.sound.Sound;
import galaga.Galaga;
import galaga.GalagaSound;

import java.util.Iterator;

public class BulletManager extends Entity implements  Iterable<Bullet> {
    
    private final Bullet[] bullets = new Bullet[8];
    private int count = 0;
    private Sound shootSound;

    public BulletManager() {
        super();
    }

    public void shoot(SpriteEntity shooter) {
        if(this.count >= this.bullets.length) {
            return;
        }
        this.shootSound.play(2.f);
        this.count++;
        this.bullets[this.count - 1] = new Bullet(shooter);
    }

    
    @Override
    public Iterator<Bullet> iterator() {
        return new Iterator<Bullet>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < count;
            }

            @Override
            public Bullet next() {
                return bullets[index++];
            }
        };
    }

    public void remove(Bullet bullet) {
        for (int i = 0; i < this.count; i++) {
            if (this.bullets[i] == bullet) {
                this.bullets[i] = this.bullets[this.count - 1];
                this.bullets[this.count - 1] = null;
                this.count--;
                return;
            }
        }
    }

    public void removeAll(Iterable<Bullet> removeBullets) {
        for (Bullet bullet : removeBullets) {
            this.remove(bullet);
        }
    }

    public boolean isEmpty() {
        return this.count == 0;
    }

    @Override
    public boolean init() {
        this.shootSound = Galaga.getContext().getResource().get(GalagaSound.fighter_shot1);
        if(this.shootSound == null) {
            return false;
        }
        return true;
    }

    @Override
    public void update(float dt) {
        throw new UnsupportedOperationException("BulletManager.update should not be called");
    }

    @Override
    public void draw() {
        for (int i = 0; i < this.count; i++) {
            this.bullets[i].draw();
        }
    }


}
