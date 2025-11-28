package game;

import java.awt.event.KeyEvent;
import java.awt.Color;
import java.util.List;

import engine.AppContext;
import engine.Application;
import engine.utils.Time;
import game.entities.enemies.Enemy;
import game.entities.player.Player;
import game.entities.sky.Sky;
import game.level.LevelLoader;

public class Galaga extends Application {
    private LevelLoader levelLoader;

    private Sky sky;
    private Player player;
    private List<Enemy> enemies;

    @SuppressWarnings("unchecked")
    public static AppContext<State> getContext() {
        return Application.getContext();
    }

    public static void main(String[] args) throws Exception {
        Galaga game = new Galaga();
        game.start();
    }

    public Galaga() {
        super("Galaga - @jules1univ", Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        getContext().setState(new State());
    }

    @Override
    protected boolean init() {
        this.levelLoader = new LevelLoader();
        
        if(this.levelLoader.load(Config.LEVEL_1_PATH) == null) {
            return false;
        }
        if(this.levelLoader.load(Config.LEVEL_2_PATH) == null) {
            return false;
        }

        getContext().getRenderer().setFont("Consolas", 18);

        this.sky = new Sky(Config.DEFAULT_SKY_GRID_SIZE);
        this.sky.init();

        this.player = new Player();
        this.player.init();

        this.enemies = this.levelLoader.getLevel( this.levelLoader.getLevelNames().get(0)).getEnemies();
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

        if (getContext().getInput().isKeyDown(KeyEvent.VK_ESCAPE)) {
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
