package game.entities.sky;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import engine.entity.Entity;
import game.Galaga;

public class Sky extends Entity {
    private List<Star> stars = new ArrayList<>();
    private int size;

    public Sky(int gridSize) {
        super();
        this.size = gridSize;
    }

    @Override
    public boolean init() {
        int width = Galaga.getContext().getFrame().getWidth();
        int height = Galaga.getContext().getFrame().getHeight();

        for (int x = 0; x < width; x += this.size) {
            for (int y = 0; y < height; y += this.size) {
                int offsetX = (int) (Math.random() * this.size);
                int offsetY = (int) (Math.random() * this.size);
                int pointX = x + offsetX;
                int pointY = y + offsetY;

                if (pointX < width && pointY < height) {
                    Color color = new Color(
                            (int) (Math.random() * 256),
                            (int) (Math.random() * 256),
                            (int) (Math.random() * 256));

                    Star star = new Star(pointX, pointY, (int) Galaga.DEFAULT_SPRITE_SCALE, color);
                    star.init();
                    this.stars.add(star);
                }
            }
        }

        return true;
    }

    @Override
    public void update(double dt) {
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
