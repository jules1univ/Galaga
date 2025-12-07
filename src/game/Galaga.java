package game;

import engine.AppContext;
import engine.Application;
import engine.elements.ui.text.Text;
import engine.elements.ui.text.TextPosition;
import engine.graphics.font.FontResource;
import engine.graphics.sprite.Sprite;
import engine.resource.Resource;
import engine.resource.ResourceManager;
import engine.resource.ResourceVariant;
import engine.utils.Position;
import game.entities.enemies.Enemy;
import game.entities.player.Player;
import game.entities.sky.Sky;
import game.level.Level;
import game.level.LevelResource;
import game.ui.game.FUD;
import game.ui.game.HUD;
import game.ui.menu.Menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.util.List;

public class Galaga extends Application {

    private Sky sky;
    private Player player;
    private List<Enemy> enemies;
    // TODO: add bullets
    // TODO: add particles & create a particle system in engine

    private FUD fud;
    private HUD hud;
    private Menu menu;
    private Text loadingText;

    private volatile boolean loading = true;

    @SuppressWarnings("unchecked")
    public static AppContext<State> getContext() {
        return Application.getContext();
    }

    public static void main(String[] args) throws Exception {
        Galaga game = new Galaga();
        game.start();
    }

    public Galaga() {
        super(Config.WINDOW_TITLE, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        getContext().setState(new State());
    }

    private boolean load() {
        if (!this.player.init()) {
            return false;
        }

        Level level = getContext().getResource().get(Config.LEVEL_1);
        this.enemies = level.getEnemies();
        for (Enemy enemy : this.enemies) {
            if (!enemy.init()) {
                return false;
            }
        }

        this.menu = new Menu();
        if (!this.menu.init()) {
            return false;
        }

        this.fud = new FUD();
        if (!this.fud.init()) {
            return false;
        }

        this.hud = new HUD();
        if (!this.hud.init()) {
            return false;
        }

        Sprite medal = Galaga.getContext().getResource().get(Config.MEDAL_SPRITE);
        getContext().getFrame().setIconImage(medal.getImage());
        this.loading = false;
        return true;
    }

    @Override
    protected boolean init() {
        Font[] defaultFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        if (defaultFonts.length == 0) {
            return false;
        }

        Font defaultFont = defaultFonts[0].deriveFont(Config.SIZE_FONT_LARGE);
        this.loadingText = new Text("Loading", Position.of(
                getContext().getFrame().getWidth() / 2,
                getContext().getFrame().getHeight() / 2), Color.WHITE, defaultFont);
        this.loadingText.setCenter(TextPosition.CENTER, TextPosition.CENTER);

        ResourceManager rm = getContext().getResource();
        rm.register("levels", LevelResource.class);

        rm.add(Config.DEFAULT_FONT, "font", (ResourceVariant variant, Resource<?> rawRes) -> {
            if (variant != null && variant.getName().equals(Config.VARIANT_FONT_LARGE)) {
                FontResource font = (FontResource) rawRes;
                this.loadingText.setFont(font.getData());
            }
        });

        rm.add(Config.SHIP_SPRITE, "sprite");
        rm.add(Config.MEDAL_SPRITE, "sprite");
        rm.add(Config.ENEMY_SPRITES, "sprite");

        rm.add(Config.LEVEL_1, "levels");
        rm.add(Config.LEVEL_2, "levels");

        this.sky = new Sky(Config.SIZE_SKY_GRID);
        if (!this.sky.init()) {
            return false;
        }

        getContext().getState().player = new Player();
        this.player = getContext().getState().player;

        rm.load(() -> {
            if (!this.load()) {
                this.stop();
            }
        }, Config.SPEED_LOADING);
        return true;
    }

    @Override
    protected void update(double dt) {
        if (this.loading) {
            this.loadingText.setText(
                    String.format(
                            "Loading... %.2f%% (%s)",
                            getContext().getResource().getProgress() * 100.0f,
                            getContext().getResource().getStatus()
                    ));
            return;
        }

        this.sky.update(dt);
        if (this.menu.isVisible()) {
            this.menu.update(dt);
            return;
        }

        this.player.update(dt);

        for (Enemy enemy : this.enemies) {
            enemy.update(dt);
        }

        // TODO: update bullets & collisions
        if (getContext().getInput().isKeyDown(KeyEvent.VK_ESCAPE)) {
            this.stop();
        }

        this.hud.update(dt);
        this.fud.update(dt);
    }

    @Override
    protected void draw() {
        if (this.loading) {
            this.loadingText.draw();
            return;
        }

        this.sky.draw();
        if (this.menu.isVisible()) {
            this.menu.draw();
            return;
        }

        this.player.draw();

        for (Enemy enemy : this.enemies) {
            enemy.draw();
        }

        // TODO: draw bullets here
        // TODO: display the level name at the beginning
        // TODO: show the new medal earned when a level is completed
        this.hud.draw();
        this.fud.draw();

        // DEBUG DRAW TO VIEW ELEMENTS ALIGNMENT
        // getContext().getRenderer().drawGrid(Config.SIZE_SKY_GRID, Color.WHITE);
        //  getContext().getRenderer().drawCross(Color.RED);
    }

}
