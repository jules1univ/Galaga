package galaga.entities.enemies.types;

import engine.resource.sound.Sound;
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
    
    private boolean captureSoundPlayed = false;
    private Sound captureSound;

    public EnemyMoth(EnemyConfig config) {
        super(config, GalagaSound.enemy_big_die);
        assert config.getType() == EnemyType.MOTH;
    }

    public void capture(Player player) {
        this.player = player;
        this.player.setMove(false);
    }

    @Override
    public boolean init()
    {
        if(!super.init()) {
            return false;
        }
        this.captureSound = Galaga.getContext().getResource().get(GalagaSound.enemy_capture_player);
        return this.captureSound != null;
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
            if(!this.captureSoundPlayed) {
                this.captureSound.play();
                captureSoundPlayed = true;
            }

            this.player.setPosition(Position.of(
                    this.getCenter().getX() + this.player.getScaledSize().getWidth() / 2.f,
                    this.getPosition().getY() + this.getScaledSize().getHeight()));

            this.animateToLockPosition(dt);
            if (this.isInLockPosition()) {
                this.captureSound.stop();

                this.player.onCollideWithEnemy(this);
                this.player.setMove(true);
                this.player = null;

                Position capturePosition = Position.of(
                    this.getCenter().getX() + this.getScaledSize().getWidth() / 2.f,
                    this.getPosition().getY() + this.getScaledSize().getHeight());
                    
                EnemyCapturedPlayer capturedPlayer = EnemyFactory.createCapturedPlayer(this.config, capturePosition);
                capturedPlayer.setCapturePosition(capturePosition);
                
                Galaga.getContext().getState().level.spawnEnemy(capturedPlayer);

                this.state = EnemyState.FORMATION;
            }

            return;
        }

        this.timer +=  dt;
        this.target = Galaga.getContext().getState().player.getCenter().copy();
        boolean ready = this.timer >= this.config.getLevel().getAttackCooldown();

        float distance = this.position.distance(target);
        float speedFactor = ready ? 2.f : 0.2f;
        float scaledSpeed = (this.config.getSpeed() * speedFactor) *  dt + distance *  dt;

        this.position.moveTo(target, scaledSpeed);
        this.angle = this.position.angleTo(target) + 90.f;

    }

}
