package game.entities.enemies;

import engine.utils.Position;

public class EnemyButterFly extends Enemy {

    private final float initalDistance;

    public EnemyButterFly(boolean leftAnimation, Position lock, int value, float speed) {
        super(EnemyType.BUTTERFLY, leftAnimation, lock, value, speed);

        this.initalDistance = this.position.distance(this.lock);
    }

    @Override
    public void update(double dt) {
        float distance = this.position.distance(this.lock);
        float distancePercent = distance / this.initalDistance;

        if (distancePercent > 0.01f) {
            float scaledSpeed = this.speed * (float) dt + distance * (float) dt;
            this.position.moveTo(this.lock, scaledSpeed);
            if(this.leftAnimation){
            this.angle = this.position.angleTo(this.lock) - 45;
            }else{
            this.angle = this.position.angleTo(this.lock) + 45;
            }
        } else {
            this.angle = 180.f;
            this.position = this.lock.copy();
        }

    }

}
