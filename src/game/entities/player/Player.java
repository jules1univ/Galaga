package game.entities.player;

import engine.Application;
import engine.elements.entity.SpriteEntity;
import engine.utils.Position;
import game.Config;
import game.Galaga;
import game.entities.enemies.Enemy;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

public final class Player extends SpriteEntity {

    private int life;
    private int score;
    private int medals;
    private float cooldownTimer = Config.DELAY_SHOOT_PLAYER;
    private Font debugFont;

    public Player() {
        super();
        this.angle = 0.f;
        this.scale = Config.SPRITE_SCALE_DEFAULT;

        this.life = Config.PLAYER_INITIAL_LIFE;
        this.medals = 0;
        this.score = 0;
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
    }

    public void onHit() {
        this.life--;
        if (this.life < 0) {
            this.life = 0;
        }
    }

    public boolean isDead() {
        return this.life <= 0;
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
    public void update(double dt) {
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
        if (Galaga.getContext().getInput().isKeyDown(KeyEvent.VK_SPACE)) {
            if (this.cooldownTimer >= Config.DELAY_SHOOT_PLAYER) {
                Galaga.getContext().getState().bullets.shoot(this);
                this.cooldownTimer = 0.f;
            }

        }
    }

    @Override
    public void draw() {
        super.draw();
        if (Application.DEBUG_MODE) {
            float delayPercent = Math.clamp((1.f - (this.cooldownTimer / Config.DELAY_SHOOT_PLAYER)), 0.f, 1.f) * 100.f;
            String debugText = String.format("%.2f%%", delayPercent);
            Application.getContext().getRenderer().drawText(debugText, this.getCenter(), Color.WHITE, this.debugFont);
        }
    }

}
