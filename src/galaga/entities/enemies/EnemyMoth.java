package galaga.entities.enemies;

import engine.utils.Position;
import galaga.Galaga;
import galaga.entities.player.Player;

public class EnemyMoth extends Enemy {

    private final float attackCooldown;
    private float attackTimer = 0.f;
    private Position target;
    private Player player;

    public EnemyMoth(EnemySetting setting, float formationSpeed, float attackCooldown) {
        super(EnemyType.MOTH, setting, formationSpeed);
        this.attackCooldown = attackCooldown;
    }

    public void capture(Player player) {
        this.player = player;
        this.player.setMove(false);
    }

    @Override
    public boolean canPerformAction() {
        return this.attackCooldown > 0.f;
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

        if (this.player != null) {
            this.player.setPosition(this.getPosition().copy().add(Position.of(this.getSize())));
            this.animateToLockPosition(dt);
            if (this.isInLockPosition()) {
                this.player.onCollideWithEnemy(this);
                this.player.setMove(true);
                this.player = null;
                this.state = EnemyState.FORMATION;
            }
            return;
        }

        this.attackTimer += (float) dt;
        this.target = Galaga.getContext().getState().player.getCenter().copy();
        boolean ready = this.attackTimer >= this.attackCooldown;

        float distance = this.position.distance(target);
        float speedFactor = ready ? 2.f : 0.2f;
        float scaledSpeed = (this.speed * speedFactor) * (float) dt + distance * (float) dt;

        this.position.moveTo(target, scaledSpeed);
        this.angle = this.position.angleTo(target) + 90.f;

    }

}
