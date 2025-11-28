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

        this.width = size;
        this.height = size;
        this.color = color;
    }

    @Override
    public boolean init() {
        this.blinkDelay = Config.STAR_MIN_BLINK_DELAY + (float) Math.random() * Config.STAR_MAX_BLINK_DELAY;
        this.active = Math.random() > 0.5;
        return true;
    }

    @Override
    public void update(double dt) {
        this.time += dt;
        if (this.time > this.blinkDelay) {
            this.time = 0.0f;
            this.active = !this.active;
            this.y = this.initialY;

            // Not updated to save performance
            // this.blinkDelay = MIN_BLINK_DELAY + (float) Math.random() * MAX_BLINK_DELAY;
        }
        this.y += dt * Config.STAR_MOVE_SPEED;
    }

    @Override
    public void draw() {
        if (!this.active) {
            return;
        }
        Galaga.getContext().getRenderer().drawRect(this.x, this.y, this.width, this.height, this.color);
    }

}
