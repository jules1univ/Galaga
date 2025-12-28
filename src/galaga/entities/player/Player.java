package galaga.entities.player;

import engine.Application;
import engine.elements.entity.SpriteEntity;
import engine.graphics.Renderer;
import engine.resource.sound.Sound;
import engine.utils.Collision;
import engine.utils.Position;
import engine.utils.Size;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaSound;
import galaga.entities.bullet.Bullet;
import galaga.entities.bullet.BulletShooter;
import galaga.entities.enemies.Enemy;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

public final class Player extends SpriteEntity implements BulletShooter {

    private int life;
    private int score;
    private int medals;

    private boolean shootActive;
    private boolean moveActive;

    private float cooldownTimer;
    private float hitTimer;

    private float velocityX;

    private Sound dieSound;

    private Font debugFont;

    public Player() {
        super();
        this.angle = 0.f;
        this.scale = Config.SPRITE_SCALE_DEFAULT;

        this.shootActive = false;
        this.moveActive = true;

        this.life = Config.PLAYER_INITIAL_LIFE;
        this.cooldownTimer = Config.DELAY_SHOOT_PLAYER;
        this.medals = 0;
        this.score = 0;
    }

    public boolean isShootingActive() {
        return this.shootActive;
    }

    public void setShooting(boolean shootActive) {
        this.shootActive = shootActive;
    }

    public void setMove(boolean active) {
        this.moveActive = active;
        this.velocityX = 0.f;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getLife() {
        return this.life;
    }

    public int getScore() {
        return this.score;
    }

    public int getMedals() {
        return this.medals;
    }

    private void resetPosition() {
        this.position = Position.of(
                (Galaga.getContext().getFrame().getWidth() - this.getScaledSize().getWidth()) / 2,
                Galaga.getContext().getFrame().getHeight() - this.getScaledSize().getHeight() - Config.HEIGHT_FUD);
        this.position.addX(this.getScaledSize().getWidth() / 2);
        this.position.addY(-this.getScaledSize().getHeight() / 2);
        this.velocityX = 0.f;
    }

    public void onFinishLevel() {
        this.medals++;
        this.resetPosition();
    }

    public boolean isDead() {
        return this.life <= 0;
    }

    public boolean isReswawning() {
        return this.hitTimer > 0.f;
    }

    private void onHit() {
        this.dieSound.play();
        Galaga.getContext().getState().particles.createExplosion(this);

        this.resetPosition();
        this.hitTimer = Config.DELAY_PLAYER_HIT;

        this.life--;
        if (this.life < 0) {
            this.life = 0;
        }
    }

    @Override
    public Position getBulletSpawnPosition(Size bulletSize) {
        return this.getCenter().copy().add(Position.of(
                this.getScaledSize().getWidth() / 2 - bulletSize.getWidth() / 2,
                0));
    }

    @Override
    public float getBulletSpawnAngle() {
        return this.angle;
    }

    @Override
    public void onBulletHitSelf() {
        this.onHit();
    }

    @Override
    public void onBulletHitOther(BulletShooter shooter) {
        shooter.onBulletHitSelf();
        if (shooter instanceof Enemy enemy) {
            this.score += enemy.getScoreValue();
        }
    }

    public void onCollideWithEnemy(Enemy enemy) {
        this.score += enemy.getScoreValue();
        this.onHit();
    }

    @Override
    public boolean isBulletColliding(Bullet bullet) {
        return Collision.aabb(bullet.getPosition(), bullet.getSize(), this.getCenter(), this.getScaledSize());
    }

    @Override
    public boolean init() {
        this.sprite = Galaga.getContext().getState().shipSkin;
        if (this.sprite == null) {
            return false;
        }
        this.size = this.sprite.getSize();
        this.resetPosition();

        this.dieSound = Galaga.getContext().getResource().get(GalagaSound.player_die);
        if (this.dieSound == null) {
            return false;
        }

        this.debugFont = Galaga.getContext().getResource().get(Config.FONTS, Config.VARIANT_FONT_TEXT);

        return true;
    }

    @Override
    public void update(float dt) {
        if (this.hitTimer > 0.f) {
            this.hitTimer -= dt;
            return;
        }

        if (!this.moveActive) {
            return;
        }

        boolean moving = false;
        if (Galaga.getContext().getInput().isKeyDown(KeyEvent.VK_LEFT)) {
            this.velocityX -= Config.SPEED_ACCELERATION_PLAYER * dt;
            moving = true;
        }

        if (Galaga.getContext().getInput().isKeyDown(KeyEvent.VK_RIGHT)) {
            this.velocityX += Config.SPEED_ACCELERATION_PLAYER * dt;
            moving = true;
        }

        if (!moving) {
            this.velocityX -= this.velocityX * Config.SPEED_DAMPING_PLAYER * dt;
            if (Math.abs(this.velocityX) < Config.SPEED_MIN_ACCELERATION_PLAYER) {
                this.velocityX = 0f;
            }
        }

        this.velocityX = Math.clamp(this.velocityX, -Config.SPEED_MAX_ACCELERATION_PLAYER,
                Config.SPEED_MAX_ACCELERATION_PLAYER);
        this.position.addX(this.velocityX * dt);

        this.position.clampX(
                this.size.getWidth(),
                Galaga.getContext().getFrame().getWidth() - this.size.getWidth());

        this.cooldownTimer += dt;
        if (Galaga.getContext().getInput().isKeyDown(KeyEvent.VK_SPACE) && this.shootActive) {
            if (this.cooldownTimer >= Config.DELAY_SHOOT_PLAYER) {
                Galaga.getContext().getState().bullets.shoot(this);
                this.cooldownTimer = 0.f;
            }

        }
    }

    @Override
    public void draw(Renderer renderer) {
        if (this.hitTimer <= 0.f) {
            super.draw(renderer);
        } else {
            renderer.drawLoadingCircle(
                    this.getPosition(),
                    this.getScaledSize().getWidth() / 2.f,
                    Color.RED,
                    60,
                    1.f - (this.hitTimer / Config.DELAY_PLAYER_HIT));
        }

        if (Application.DEBUG_MODE) {
            String debugText = String.format("VEL: %.2f | FIRE: %.2f",
                    this.velocityX,
                    this.cooldownTimer);
            renderer.drawText(debugText, this.getCenter(), Color.WHITE, this.debugFont);
        }
    }

}
