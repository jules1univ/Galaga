package game.entities.sky;

import engine.elements.entity.Entity;
import engine.utils.Position;
import game.Config;
import game.Galaga;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public final class Sky extends Entity {
    private final List<Star> stars = new ArrayList<>();
    private final int gridSize;

    public Sky(int gridSize) {
        super();
        this.gridSize = gridSize;
    }

    @Override
    public boolean init() {
        int width = Galaga.getContext().getFrame().getWidth();
        int height = Galaga.getContext().getFrame().getHeight();

        for (int x = 0; x < width; x += this.gridSize) {
            for (int y = 0; y < height; y += this.gridSize) {
                int offsetX = (int) (Math.random() * this.gridSize);
                int offsetY = (int) (Math.random() * this.gridSize);
                int pointX = x + offsetX;
                int pointY = y + offsetY;

                if (pointX < width && pointY < height) {
                    Color color = new Color(
                            (int) (Math.random() * 256),
                            (int) (Math.random() * 256),
                            (int) (Math.random() * 256));

                    Star star = new Star(Position.of(pointX, pointY), (int) Config.SPRITE_SCALE_DEFAULT, color);
                    star.init();
                    this.stars.add(star);
                }
            }
        }

        return true;
    }

    @Override
    public void update(float dt) {
        for (Star star : stars) {
            star.update(dt);
        }
    }

    @Override
    public void draw() {
        for (Star star : stars) {
            star.draw();
        }
    }

}
