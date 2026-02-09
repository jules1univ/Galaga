package galaga.entities.enemies.types;

import engine.utils.Position;
import galaga.Config;
import galaga.Galaga;
import galaga.entities.bullet.BulletShooter;
import galaga.entities.enemies.Enemy;
import galaga.entities.enemies.EnemyConfig;
import galaga.entities.enemies.EnemyState;
import galaga.entities.enemies.EnemyType;
import galaga.resources.sound.GalagaSound;

public class EnemyCapturedPlayer extends Enemy {
    private float timer;
    private boolean playerKilled;

    public EnemyCapturedPlayer(EnemyConfig config) {
        super(config, GalagaSound.enemy_medium_die);
        assert config.getType() == EnemyType.CAPTURED_PLAYER;

        this.state = EnemyState.RETURNING;
        this.playerKilled = false;
    }

    public void setCapturePosition(Position position) {
        this.position = position;
    }

    @Override
    public boolean canPerformAction() {
        return config.getLevel().getMissileCooldown() > 0.f && !Galaga.getContext().getState().player.isReswawning();
    }

    @Override
    public boolean canRemove() {
        return this.position.getY()
                + Config.POSITION_ENEMY_CAPTURED_PLAYER_MAX_Y >= Galaga.getContext().getState().player.getPosition()
                        .getY()
                || this.playerKilled;
    }

    @Override
    public void onBulletHitOther(BulletShooter player) {
        super.onBulletHitOther(player);
        this.playerKilled = true;
    }

    @Override
    protected void updateAction(float dt) {
        if (this.state != EnemyState.ATTACKING) {
            this.state = EnemyState.RETURNING;
            return;
        }

        Position target = Galaga.getContext().getState().player.getCenter().copy();

        float distance = this.position.distance(target);

        this.position.moveTo(target, this.config.getSpeed() * distance * dt);
        this.angle = this.position.angleTo(target) - 90.f;

        if (this.playerKilled) {
            return;
        }
        this.timer += dt;
        if (this.timer >= this.config.getLevel().getMissileCooldown()
                * Config.DELAY_ENEMY_CAPTURED_PLAYER_SHOOT_FACTOR) {
            this.timer = 0.f;
            Galaga.getContext().getState().bullets.shoot(this);
        }
    }

}
