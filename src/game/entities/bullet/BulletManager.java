package game.entities.bullet;

import java.util.ArrayList;
import java.util.List;

import engine.elements.entity.Entity;

public class BulletManager extends Entity {
    private final List<Bullet> bullets = new ArrayList<>();
    

    public BulletManager() {
    }

    public void shoot(Entity shooter) {
        this.bullets.add(new Bullet(shooter));
    }

}
