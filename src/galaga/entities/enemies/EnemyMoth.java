package galaga.entities.enemies;

import engine.utils.Position;
import engine.utils.logger.Log;
import galaga.Config;
import galaga.Galaga;

public class EnemyMoth extends Enemy {

    private final float attackCooldown;
    private float attackTimer = 0.f;
    private Position target;

    public EnemyMoth(Position lock, int actionIndex ,int enterIndex, int value, float speed, float formationSpeed, float attackCooldown) {
        super(EnemyType.MOTH, lock, actionIndex, enterIndex, value, speed, formationSpeed);
        this.attackCooldown = attackCooldown;
    }

    @Override
    protected void updateAction(float dt) {
        if (this.state != EnemyState.ATTACKING) {
            return;
        }

        if (this.attackCooldown < 0.f) {
            this.state = EnemyState.RETURNING;
            return;
        }

        this.attackTimer += (float) dt;
        this.target = Galaga.getContext().getState().player.getCenter().copy();
        boolean ready = this.attackTimer >= this.attackCooldown;
        Log.message(this.attackTimer + "/" + this.attackCooldown);

        float distance = this.position.distance(target);
        float speedFactor = ready ? 2.f : 0.2f;
        float scaledSpeed = (this.speed * speedFactor) * (float) dt + distance * (float) dt;

        this.position.moveTo(target, scaledSpeed);
        this.angle = this.position.angleTo(target) + 90.f;

        if (this.position.distance(target) <= Config.POSITION_NEAR_THRESHOLD) {
            this.state = EnemyState.RETURNING;
        }

    }
}
