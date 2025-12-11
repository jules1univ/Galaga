package galaga.entities.sky;

import engine.elements.entity.Entity;
import engine.utils.Position;
import engine.utils.Size;
import galaga.Config;
import galaga.Galaga;
import java.awt.Color;

public final class Star extends Entity {

    private final Color color;
    private final float initialY;

    private float time;
    private float blinkDelay;
    private boolean active;


    public Star(Position position, float size, Color color) {
        super();

        this.position = position;
        this.initialY = position.getY();

        this.time = .0f;
        this.blinkDelay = .0f;
        this.active = false;

        this.size = Size.of(size);
        this.color = color;
    }

    @Override
    public boolean init() {
        this.blinkDelay = Config.TIME_BLINKSTAR_MIN + (float) Math.random() * Config.TIME_BLINKSTAR_MAX;
        this.time = Config.TIME_BLINKSTAR_MIN/2;
        return true;
    }

    @Override
    public void update(float dt) {
        this.time += dt;
        if (this.time > this.blinkDelay) {
            this.time = .0f;
            this.active = !this.active;
            this.position.setY(this.initialY);
        }
        this.position.addY(Config.SPEED_STAR * (float)dt);

        // TODO: add woobly effect when player hit an enemy
        // this.x  = this.x + (float)(Math.sin(this.y / 50.0f) * 0.5f);
    }

    @Override
    public void draw() {
        if (!this.active) {
            return;
        }
        Galaga.getContext().getRenderer().drawRect(this.getPosition(), this.getSize(), this.color);
    }

}
