package galaga.entities.particles;

import engine.elements.entity.Entity;
import engine.utils.Position;
import galaga.Config;
import galaga.Galaga;
import java.awt.Color;

public class Particle extends Entity {

    private Color color;
    private float duration;
    private final float speed;
    private final float angle;

    public Particle(Position position, Color color, float duration, float speed, float angle) {
        this.position = position.copy();
        this.size = Config.SIZE_PARTICLE.copy();
        this.color = color;
        this.duration = duration;
        this.speed = speed;
        this.angle = angle;
    }

    public boolean isExpired() {
        return this.duration <= 0;
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public void update(float dt) {

        this.position.moveTo(this.angle, this.speed *  dt);


        this.duration -= dt;
        if (this.duration <= 0) {
            this.color = new Color(0, 0, 0, 0);
            return;
        }
        this.color = new Color(
                this.color.getRed(),
                this.color.getGreen(),
                this.color.getBlue(),
                Math.max(0, (int) (this.color.getAlpha() - (255 * dt / 1.0f)))
        );
    }

    @Override
    public void draw() {
        Galaga.getContext().getRenderer().drawRect(this.position, this.size, this.color);
    }

}
