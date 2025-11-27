package game;

import java.awt.Color;

import engine.Application;
import engine.utils.Time;
import game.level.LevelLoader;
import game.sky.Sky;

public class Game extends Application {
    public static final float DEFAULT_SPRITE_SCALE = 2.5f;
    public static final int DEFAULT_SKY_GRID_SIZE = 50;

    private static final int WINDOW_WIDTH = 700;
    private static final int WINDOW_HEIGHT = 700;
    private LevelLoader levelLoader;
    private Sky sky;

    public static void main(String[] args) throws Exception {
        Game game = new Game();
        game.start();
    }

    public Game() {
        super("Galaga - @jules1univ", WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    @Override
    protected boolean init() {
        this.levelLoader = new LevelLoader(this.ctx);
        String level1 = this.levelLoader.load(".\\resources\\levels\\level1.lvl");
        if (level1 == null) {
            return false;
        }

        this.levelLoader.setup(level1);
        if (!this.ctx.entityManager.init()) {
            return false;
        }

        this.sky = new Sky(this.ctx, DEFAULT_SKY_GRID_SIZE);
        this.sky.init();
        return true;
    }

    @Override
    protected void update(double dt) {
        this.sky.update(dt);
        this.ctx.entityManager.update(dt);

        if (this.ctx.input.isKeyDown(java.awt.event.KeyEvent.VK_ESCAPE)) {
            this.stop();
        }
    }

    @Override
    protected void draw() {
        this.sky.draw();
        this.ctx.entityManager.draw();

        this.ctx.renderer.drawText(String.format("FPS: %.2f", Time.getFrameRate()), 10, 10, Color.WHITE);
    }

}
