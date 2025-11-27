package game;

import java.awt.Color;

import engine.AppContext;
import engine.Application;
import engine.utils.Time;
import game.entities.player.Player;
import game.level.LevelLoader;
import game.sky.Sky;

public class Galaga extends Application {
    public static final float DEFAULT_SPRITE_SCALE = 2.5f;
    public static final int DEFAULT_SKY_GRID_SIZE = 50;

    private static final int WINDOW_WIDTH = 700;
    private static final int WINDOW_HEIGHT = 700;
    private LevelLoader levelLoader;

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

        getContext().getState().sky = new Sky(DEFAULT_SKY_GRID_SIZE);
        getContext().getState().sky.init();

        getContext().getState().player = new Player();
        getContext().getState().player.init();

        return true;
    }

    @Override
    protected void update(double dt) {
        getContext().getState().sky.update(dt);
        getContext().getState().player.update(dt);

        if (getContext().getInput().isKeyDown(java.awt.event.KeyEvent.VK_ESCAPE)) {
            this.stop();
        }
    }

    @Override
    protected void draw() {
        getContext().getState().sky.draw();

        getContext().getState().player.draw();
        getContext().getRenderer().drawText(String.format("FPS: %.2f", Time.getFrameRate()), 10, 10, Color.WHITE);
    }

}
