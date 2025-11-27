package game.sky;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import engine.AppContext;
import engine.entity.Entity;
import game.Game;

public class Sky extends Entity<SkyObjectType> {
    private List<Star> stars = new ArrayList<>();
    private int size;

    public Sky(AppContext ctx, int gridSize) {
        super(ctx);
        this.size = gridSize;
        this.type = SkyObjectType.SKY;
    }

    @Override
    public boolean init() {
        int width = this.ctx.frame.getWidth();
        int height = this.ctx.frame.getHeight();

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

                    Star star = new Star(this.ctx, pointX, pointY, (int) Game.DEFAULT_SPRITE_SCALE, color);
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
