package galaga.entities.sky;

import engine.elements.entity.Entity;
import engine.graphics.Renderer;
import engine.utils.Position;
import galaga.Config;
import galaga.Galaga;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public final class Sky extends Entity {

    private final List<Star> stars = new ArrayList<>();
    private final int gridSize;
    private boolean activeColor = false;

    public Sky(int gridSize) {
        this.gridSize = gridSize;
    }

    public void restoreColor() {
        this.activeColor = false;
        for (Star star : stars) {
            int r = 180 + (int) (Math.random() * 75);
            int g = 180 + (int) (Math.random() * 75);
            int b = 180 + (int) (Math.random() * 75);
            Color color = new Color(r, g, b);
            star.setColor(color);
        }
    }

    public void setColor(Color color) {
        this.activeColor = true;
        for (Star star : stars)
            star.setColor(color);
    }

    public boolean isActiveColor() {
        return this.activeColor;
    }

    @Override
    public boolean init() {
        int width = Galaga.getContext().getFrame().getWidth();
        int height = Galaga.getContext().getFrame().getHeight();

        for (int x = 0; x < width; x += gridSize) {
            for (int y = 0; y < height; y += gridSize) {

                int px = x + (int) (Math.random() * gridSize);
                int py = y + (int) (Math.random() * gridSize);

                if (px < width && py < height) {

                    int r = 180 + (int) (Math.random() * 75);
                    int g = 180 + (int) (Math.random() * 75);
                    int b = 180 + (int) (Math.random() * 75);
                    Color color = new Color(r, g, b);

                    Star s = new Star(Position.of(px, py), Config.SPRITE_SCALE_DEFAULT, color);
                    s.init();
                    stars.add(s);
                }
            }
        }
        return true;
    }

    @Override
    public void update(float dt) {
        for (Star s : stars)
            s.update(dt);
    }

    @Override
    public void draw(Renderer renderer) {
        for (Star s : stars)
            s.draw(renderer);
    }
}
