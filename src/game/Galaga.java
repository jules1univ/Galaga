package game;

import java.awt.Color;
import java.util.List;

import engine.AppContext;
import engine.Application;
import engine.utils.Time;
import game.entities.enemies.Enemy;
import game.entities.player.Player;
import game.level.LevelLoader;
import game.sky.Sky;

public class Galaga extends Application {
    public static final float DEFAULT_SPRITE_SCALE = 2.5f;
    public static final int DEFAULT_SKY_GRID_SIZE = 50;

    private static final int WINDOW_WIDTH = 700;
    private static final int WINDOW_HEIGHT = 700;
    private LevelLoader levelLoader;

    private Sky sky;
    private Player player;
    private List<Enemy> enemies;

    @SuppressWarnings("unchecked")
    public static AppContext<GalagaState> getContext() {
        return Application.getContext();
    }

    public static void main(String[] args) throws Exception {
        Galaga game = new Galaga();
        game.start();
    }

    public Galaga() {
        super("Galaga - @jules1univ", WINDOW_WIDTH, WINDOW_HEIGHT);
        getContext().setState(new GalagaState());
    }

    @Override
    protected boolean init() {
        this.levelLoader = new LevelLoader();
        String level1 = this.levelLoader.load(".\\resources\\levels\\level1.lvl");
        if (level1 == null) {
            return false;
        }

        getContext().getRenderer().setFont("Arial Bold", 24);

        this.sky = new Sky(DEFAULT_SKY_GRID_SIZE);
        this.sky.init();

        this.player = new Player();
        this.player.init();

        this.enemies = this.levelLoader.getLevel(level1).getEnemies();
        for (Enemy enemy : this.enemies) {
            enemy.init();
        }
        return true;
    }

    @Override
    protected void update(double dt) {
        this.sky.update(dt);
        this.player.update(dt);

        for (Enemy enemy : this.enemies) {
            enemy.update(dt);
        }

        if (getContext().getInput().isKeyDown(java.awt.event.KeyEvent.VK_ESCAPE)) {
            this.stop();
        }
    }

    @Override
    protected void draw() {
        this.sky.draw();

        this.player.draw();

        for (Enemy enemy : this.enemies) {
            enemy.draw();
        }

        getContext().getRenderer().drawText(String.format("FPS: %.2f", Time.getFrameRate()), 10, 10, Color.WHITE);
    }

}
