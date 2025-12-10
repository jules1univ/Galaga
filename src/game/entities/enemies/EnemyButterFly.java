package game.entities.enemies;

import engine.utils.Position;
import game.Config;
import game.Galaga;

public class EnemyButterFly extends Enemy {

    private final float missileCooldown;
    private float missileTimer = 0.f;
    private final Position target;

    public EnemyButterFly(Position lock, int value, float speed, float formationSpeed, float missileCooldown) {
        super(EnemyType.BUTTERFLY, lock, value, speed, formationSpeed);
        this.missileCooldown = missileCooldown;
        this.target = this.lock.copy().setY(Config.WINDOW_HEIGHT - Config.HEIGHT_FUD * 2);
    }

    @Override
    protected void updateAction(float dt) {
        if (this.state != EnemyState.ATTACKING) {
            return;
        }

        float distance = this.position.distance(target);
        float scaledSpeed = this.speed * (float) dt + distance * (float) dt;

        this.position.moveTo(target, scaledSpeed);
        this.angle = 180.f;

        if (this.position.distance(target) <= Config.POSITION_NEAR_THRESHOLD) {
            this.state = EnemyState.RETURNING;
        }

        this.missileTimer += (float) dt;
        if (this.missileTimer >= this.missileCooldown) {
            this.missileTimer = 0.f;
            Galaga.getContext().getState().bullets.shoot(this);
        }
    }

}
