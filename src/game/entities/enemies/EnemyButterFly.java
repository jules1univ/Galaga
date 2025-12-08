package game.entities.enemies;

import engine.utils.Position;
import engine.utils.logger.Log;
import game.Galaga;

public class EnemyButterFly extends Enemy {

    private final float missileCooldown;
    private float missileTimer = 0.f;

    public EnemyButterFly(Position lock, int value, float speed, float formationSpeed, float missileCooldown) {
        super(EnemyType.BUTTERFLY, lock, value, speed, formationSpeed);
        this.missileCooldown = missileCooldown;
        Log.message(" " + missileCooldown);
    }

    @Override
    protected void updateAction(double dt) {
        if (this.state != EnemyState.ATTACKING) {
            return;
        }




        this.missileTimer += (float) dt;
        if (this.missileTimer >= this.missileCooldown) {
            this.missileTimer = 0.f;
            Galaga.getContext().getState().bullets.shoot(this);
        }
    }

}
