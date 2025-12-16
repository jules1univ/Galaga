package galaga.entities.enemies.types;

import engine.utils.Position;
import galaga.Galaga;
import galaga.GalagaSound;
import galaga.entities.enemies.Enemy;
import galaga.entities.enemies.EnemyFactory;
import galaga.entities.enemies.EnemyConfig;
import galaga.entities.enemies.EnemyState;
import galaga.entities.enemies.EnemyType;
import galaga.entities.player.Player;

public class EnemyMoth extends Enemy {

    private float timer = 0.f;
    private Position target;
    private Player player;

    public EnemyMoth(EnemyConfig config) {
        super(config, GalagaSound.enemy_big_die);
        assert config.getType() == EnemyType.MOTH;
    }

    public void capture(Player player) {
        this.player = player;
        this.player.setMove(false);
    }

    @Override
    public boolean canPerformAction() {
        return this.config.getLevel().getAttackCooldown() > 0.f;
    }

    @Override
    protected void updateAction(float dt) {
        if (this.state != EnemyState.ATTACKING) {
            return;
        }

        if (this.config.getLevel().getAttackCooldown() < 0.f) {
            this.state = EnemyState.RETURNING;
            return;
        }

        if (this.player != null) {
            this.player.setPosition(Position.of(
                this.getCenter().getX() + this.player.getScaledSize().getWidth() / 2.f,
                this.getPosition().getY() + this.getScaledSize().getHeight()
            ));

            this.animateToLockPosition(dt);
            if (this.isInLockPosition()) {
                this.player.onCollideWithEnemy(this);
                this.player.setMove(true);
                this.player = null;
                this.state = EnemyState.FORMATION;
            }

            Galaga.getContext().getState().level.spawnEnemy(EnemyFactory.createCapturedPlayer(this.config));
            return;
        }

        this.timer += (float) dt;
        this.target = Galaga.getContext().getState().player.getCenter().copy();
        boolean ready = this.timer >= this.config.getLevel().getAttackCooldown();

        float distance = this.position.distance(target);
        float speedFactor = ready ? 2.f : 0.2f;
        float scaledSpeed = (this.config.getSpeed() * speedFactor) * (float) dt + distance * (float) dt;

        this.position.moveTo(target, scaledSpeed);
        this.angle = this.position.angleTo(target) + 90.f;

    }

}
