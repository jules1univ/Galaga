package game.entities.sky;

import java.awt.Color;

import engine.entity.Entity;
import game.Galaga;
import game.Config;

public class Star extends Entity {

    private Color color;
    private float time;
    private float blinkDelay;
    private boolean active;

    private float initialY;

    public Star(float x, float y, float size, Color color) {
        super();

        this.x = x;
        this.y = y;
        this.initialY = y;

        this.time = 0.0f;
        this.blinkDelay = 0.0f;
        this.active = false;

        this.width = size;
        this.height = size;
        this.color = color;
    }

    @Override
    public boolean init() {
        this.blinkDelay = Config.STAR_MIN_BLINK_DELAY + (float) Math.random() * Config.STAR_MAX_BLINK_DELAY;
        this.time = Config.STAR_MIN_BLINK_DELAY/2;
        return true;
    }

    @Override
    public void update(double dt) {
        this.time += dt;
        if (this.time > this.blinkDelay) {
            this.time = 0.0f;
            this.active = !this.active;
            this.y = this.initialY;
        }
        this.y += dt * Config.STAR_MOVE_SPEED;

        // TODO: add woobly effect when player hit an enemy
        // this.x  = this.x + (float)(Math.sin(this.y / 50.0f) * 0.5f);
    }

    @Override
    public void draw() {
        if (!this.active) {
            return;
        }
        Galaga.getContext().getRenderer().drawRect(this.x, this.y, this.width, this.height, this.color);
    }

}
