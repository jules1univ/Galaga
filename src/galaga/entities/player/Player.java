package galaga.entities.player;

import engine.Application;
import engine.elements.entity.SpriteEntity;
import engine.utils.Position;
import galaga.Config;
import galaga.Galaga;
import galaga.entities.enemies.Enemy;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

public final class Player extends SpriteEntity {

    private int life;
    private int score;
    private int medals;

    private boolean shootActive;
    private float cooldownTimer;
    private float hitTimer;

    private Font debugFont;

    public Player() {
        super();
        this.angle = 0.f;
        this.scale = Config.SPRITE_SCALE_DEFAULT;

        this.shootActive = false;
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

    public int getLife() {
        return this.life;
    }

    public int getScore() {
        return this.score;
    }

    public int getMedals() {
        return this.medals;
    }

    public void onKillEnemy(Enemy enemy) {
        this.score += enemy.getScoreValue();
    }

    public void onFinishLevel() {
        this.medals++;
        this.position.setX(
                (Galaga.getContext().getFrame().getWidth() - this.getScaledSize().getWidth()) / 2 + this.getScaledSize().getWidth() / 2
        );
    }

    public void onHit() {
        this.position.setX(
                (Galaga.getContext().getFrame().getWidth() - this.getScaledSize().getWidth()) / 2 + this.getScaledSize().getWidth() / 2
        );
        this.hitTimer = Config.DELAY_PLAYER_HIT;

        this.life--;
        if (this.life < 0) {
            this.life = 0;
        }
    }

    public boolean isDead() {
        return this.life <= 0;
    }

    public void reset() {
        this.life = Config.PLAYER_INITIAL_LIFE;
        this.cooldownTimer = Config.DELAY_SHOOT_PLAYER;

        this.score = 0;
        this.medals = 0;
        this.shootActive = false;
        this.position.setX(
                (Galaga.getContext().getFrame().getWidth() - this.getScaledSize().getWidth()) / 2 + this.getScaledSize().getWidth() / 2
        );
    }

    @Override
    public boolean init() {
        this.sprite = Galaga.getContext().getResource().get(Config.SPRITE_SHIP);
        if (this.sprite == null) {
            return false;
        }
        this.size = this.sprite.getSize();
        this.position = Position.of(
                (Galaga.getContext().getFrame().getWidth() - this.getScaledSize().getWidth()) / 2,
                Galaga.getContext().getFrame().getHeight() - this.getScaledSize().getHeight() - Config.HEIGHT_FUD
        );

        // sprite will be recentered by default so no need to adjust position here
        this.position.addX(this.getScaledSize().getWidth() / 2);
        this.position.addY(this.getScaledSize().getHeight() / 2);

        this.debugFont = Galaga.getContext().getResource().get(Config.FONTS, Config.VARIANT_FONT_TEXT);

        return true;
    }

    @Override
    public void update(float dt) {
        if(this.hitTimer > 0.f){
            this.hitTimer -= dt;
            return;
        }

        if (Galaga.getContext().getInput().isKeyDown(KeyEvent.VK_LEFT)) {
            this.position.addX(-Config.SPEED_PLAYER * (float) dt);
        }

        if (Galaga.getContext().getInput().isKeyDown(KeyEvent.VK_RIGHT)) {
            this.position.addX(Config.SPEED_PLAYER * (float) dt);
        }

        this.position.clampX(
                this.size.getWidth(),
                Galaga.getContext().getFrame().getWidth() - this.size.getWidth()
        );

        this.cooldownTimer += dt;
        if (Galaga.getContext().getInput().isKeyDown(KeyEvent.VK_SPACE) && this.shootActive) {
            if (this.cooldownTimer >= Config.DELAY_SHOOT_PLAYER) {
                Galaga.getContext().getState().bullets.shoot(this);
                this.cooldownTimer = 0.f;
            }

        }
    }

    @Override
    public void draw() {
        if (this.hitTimer <= 0.f) {
            super.draw();
        }else{
            Galaga.getContext().getRenderer().drawLoadingCircle(
                    this.getPosition(),
                    this.getScaledSize().getWidth() / 2.f,
                    Color.RED,
                    60,
                    1.f - (this.hitTimer / Config.DELAY_PLAYER_HIT)
            );
        }

        if (Application.DEBUG_MODE) {
            Galaga.getContext().getRenderer().drawText("", this.getCenter(), Color.WHITE, this.debugFont);
        }
    }

}
