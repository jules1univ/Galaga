package galaga.entities.bullet;

import engine.utils.Position;
import engine.utils.Size;

public interface BulletShooter {
    Position getBulletSpawnPosition(Size bulletSize);
    float getBulletSpawnAngle();

    void onBulletHitOther(BulletShooter entity);
    void onBulletHitSelf();

    boolean isBulletColliding(Bullet bullet);
}
