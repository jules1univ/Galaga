package game;

import java.awt.event.KeyEvent;
import java.util.List;

import engine.AppContext;
import engine.Application;
import engine.graphics.FontManager;
import engine.graphics.sprite.SpriteManager;
import game.entities.enemies.Enemy;
import game.entities.player.Player;
import game.entities.sky.Sky;
import game.entities.ui.FUD;
import game.entities.ui.HUD;
import game.entities.ui.Menu;
import game.level.LevelLoader;

public class Galaga extends Application {
    private LevelLoader levelLoader;

    private Sky sky;
    private Player player;
    private List<Enemy> enemies;

    private FUD fud;
    private HUD hud;
    private Menu menu;

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
        boolean fdefault = FontManager.getInstance().loadFromUrl(Config.FONT_URL, Config.TEXT_FONT_SIZE, Config.DEFAULT_FONT_ALIAS);
        if(!fdefault) {
            FontManager.getInstance().load(Config.FONT_DEFAULT, Config.TEXT_FONT_SIZE, Config.DEFAULT_FONT_ALIAS);
        }
        boolean ftitle = FontManager.getInstance().loadFromUrl(Config.FONT_URL, Config.TITLE_FONT_SIZE, Config.TITLE_FONT_ALIAS);
        if(!ftitle) {
            FontManager.getInstance().load(Config.FONT_DEFAULT, Config.TITLE_FONT_SIZE, Config.TITLE_FONT_ALIAS);
        }
        

        this.levelLoader = new LevelLoader();
        
        if(this.levelLoader.load(Config.LEVEL_1_PATH) == null) {
            return false;
        }
        if(this.levelLoader.load(Config.LEVEL_2_PATH) == null) {
            return false;
        }

        this.sky = new Sky(Config.DEFAULT_SKY_GRID_SIZE);
        if(!this.sky.init()) {
            return false;
        }

        getContext().getState().player = new Player();
        this.player = getContext().getState().player;
        if(!this.player.init()) {
            return false;
        }

        this.enemies = this.levelLoader.getLevel( this.levelLoader.getLevelNames().get(0)).getEnemies();
        for (Enemy enemy : this.enemies) {
            if(!enemy.init()) {
                return false;
            }
        }

        this.menu = new Menu();
        if(!this.menu.init()) {
            return false;
        }

        this.fud = new FUD();
        if(!this.fud.init()) {
            return false;
        }

        this.hud = new HUD();
        if(!this.hud.init()) {
            return false;
        }

        getContext().getFrame().setIconImage(SpriteManager.getInstance().get(Config.MEDAL_SPRITE_NAME).getImage());
        return true;
    }

    @Override
    protected void update(double dt) {
        this.sky.update(dt);
        if(this.menu.isVisible()) {
            this.menu.update(dt);
            return;
        }

        this.player.update(dt);

        for (Enemy enemy : this.enemies) {
            enemy.update(dt);
        }

        if (getContext().getInput().isKeyDown(KeyEvent.VK_ESCAPE)) {
            this.stop();
        }


        this.hud.update(dt);
        this.fud.update(dt);
    }

    @Override
    protected void draw() {
        this.sky.draw();
        if(this.menu.isVisible()) {
            this.menu.draw();
            return;
        }

        this.player.draw();

        for (Enemy enemy : this.enemies) {
            enemy.draw();
        }

        this.hud.draw();
        this.fud.draw();
    }

}
