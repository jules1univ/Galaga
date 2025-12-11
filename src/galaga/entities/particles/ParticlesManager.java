package galaga.entities.particles;

import engine.elements.entity.Entity;
import engine.elements.entity.SpriteEntity;
import engine.utils.Position;
import engine.utils.Size;
import java.awt.Color;
import java.util.ArrayList;

public class ParticlesManager {

    private final ArrayList<Particle> particles = new ArrayList<>();

    public ParticlesManager() {

    }

    public void createExplosion(SpriteEntity entity) {
        int total = entity.getSprite().getColors().values().stream().mapToInt(Integer::intValue).sum();

        entity.getSprite().getColors().forEach((color, count) -> {
            Size proportion = Size.of(
                    entity.getScaledSize().getWidth() * ((float) count / (float) total),
                    entity.getScaledSize().getHeight() * ((float) count / (float) total)
            );
            this.createExplosion(entity.getPosition(), proportion, color);
        });
    }

    public void createExplosion(Entity entity, Color color) {
        this.createExplosion(entity.getPosition(), entity.getSize(), color);
    }

    public void createExplosion(Position position, Size size, Color color) {
        int num = (int) ((size.getWidth() * size.getHeight()) / 4.f);
        for (int i = 0; i < num; i++) {
            float angle = (float) (Math.random() * 360);
            float speed = (float) (10 + Math.random() * 150);
            float duration = .5f + (float) (Math.random() * 1.0f);

            Particle particle = new Particle(
                    position.copy(),
                    color,
                    duration,
                    speed,
                    angle
            );
            this.particles.add(particle);
        }
    }

    public void update(float dt) {
        ArrayList<Particle> toRemove = new ArrayList<>();
        for (Particle particle : this.particles) {
            particle.update(dt);

            if (particle.isExpired()) {
                toRemove.add(particle);
            }
        }
        this.particles.removeAll(toRemove);
    }

    public void draw() {
        for (Particle particle : this.particles) {
            particle.draw();
        }
    }
}
