package game.sky;

import java.awt.Color;

import engine.entity.Entity;
import game.Galaga;

public class Star extends Entity {

    private static final float MAX_BLINK_DELAY = 10.0f;
    private static final float MIN_BLINK_DELAY = 3.0f;
    private Color color;
    private float time;
    private float blinkDelay;
    private boolean active;

    public Star(float x, float y, float size, Color color) {
        super();
        this.x = x;
        this.y = y;
        this.width = size;
        this.height = size;
        this.color = color;
    }

    @Override
    public boolean init() {
        this.blinkDelay = MIN_BLINK_DELAY + (float) Math.random() * MAX_BLINK_DELAY;
        this.active = Math.random() > 0.5;
        return true;
    }

    @Override
    public void update(double dt) {
        this.time += dt;
        if (this.time > this.blinkDelay) {
            this.time = 0.0f;
            this.blinkDelay = MIN_BLINK_DELAY + (float) Math.random() * MAX_BLINK_DELAY;
            this.active = !this.active;
        }
    }

    @Override
    public void draw() {
        if (!this.active) {
            return;
        }
        Galaga.getContext().getRenderer().drawRect(this.x, this.y, this.width, this.height, this.color);
    }

}
