package game.entities.enemies;

import engine.utils.Position;
import game.Config;
import game.Galaga;

public class EnemyBee extends Enemy {

    private final float missileCooldown;
    private float missileTimer = 0.f;

    private int zigZagIndex = 0;

    public EnemyBee(Position lock, int value, float speed, float formationSpeed, float missileCooldown) {
        super(EnemyType.BEE, lock, value, speed, formationSpeed);
        this.missileCooldown = missileCooldown;
    }

    @Override
    protected void updateAction(double dt) {
        if (this.state != EnemyState.ATTACKING) {
            return;
        }

        Position target = Config.POSITION_ZIG_ZAG.get(this.zigZagIndex);
        float distance = this.position.distance(target);
        float scaledSpeed = this.speed * (float) dt + distance * (float) dt;

        this.position.moveTo(target, scaledSpeed);
        this.angle = 180.f;

        if (this.position.distance(target) <= Config.POSITION_NEAR_THRESHOLD) {
            this.zigZagIndex++;
            if(this.zigZagIndex >= Config.POSITION_ZIG_ZAG.size()) {
                this.zigZagIndex = 0;
                this.state = EnemyState.RETURNING;
            }
        }

        this.missileTimer += (float) dt;
        if (this.missileTimer >= this.missileCooldown) {
            this.missileTimer = 0.f;
            Galaga.getContext().getState().bullets.shoot(this);
        }
    }

}
