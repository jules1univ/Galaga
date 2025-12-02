package game.entities.enemies;

import engine.utils.Position;

public class EnemyBee extends Enemy {

    private final int circleX = 325;
    private final int circleY = 200;
    private final int radius = 150;
    private float rotateAngle = 0.f;

    public EnemyBee(boolean leftAnimation, Position lock, int value, float speed) {
        super(EnemyType.BEE, leftAnimation, lock, value, speed);
        this.rotateAngle = (float) Math.atan2(lock.getY() - circleY, lock.getX() - circleX);

    }

    @Override
    public void update(double dt) {
        this.rotateAngle += this.speed * dt;

        this.position = Position.of(
                circleX + (float) Math.cos(this.rotateAngle) * radius,
                circleY + (float) Math.sin(this.rotateAngle) * radius);
        this.angle = (float) Math.toDegrees(this.rotateAngle) + 180;
    }

}
