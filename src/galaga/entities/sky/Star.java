package galaga.entities.sky;

import engine.elements.entity.Entity;
import engine.utils.Position;
import engine.utils.Size;
import galaga.Config;
import galaga.Galaga;

import java.awt.Color;

public final class Star extends Entity {

    private final Color color;
    private float time;
    private float blinkDelay;
    private boolean active;

    public Star(Position position, float size, Color color) {
        super();
        this.position = position;
        this.size = Size.of(size);
        this.color = color;

        this.time = 0f;
        this.blinkDelay = 0f;
        this.active = true;
    }

    @Override
    public boolean init() {
        this.blinkDelay = randomBlink();
        return true;
    }

    private float randomBlink() {
        return Config.TIME_BLINKSTAR_MIN +
                (float) Math.random() * (Config.TIME_BLINKSTAR_MAX - Config.TIME_BLINKSTAR_MIN);
    }

    @Override
    public void update(float dt) {
        position.addY(Config.SPEED_STAR * dt);

        if (position.getY() > Galaga.getContext().getFrame().getHeight()) {
            position.setY(0);
            position.setX((float) (Math.random() * Galaga.getContext().getFrame().getWidth()));
        }

        time += dt;
        if (time > blinkDelay) {
            active = !active;
            time = 0;
            blinkDelay = randomBlink();
        }
    }

    @Override
    public void draw() {
        if (!active) {
            return;
        }

        Galaga.getContext()
                .getRenderer()
                .drawRect(position, size, color);
    }
}
