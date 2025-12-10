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
import engine.utils.Collision;
import engine.utils.Position;
import engine.utils.Size;
import engine.utils.logger.Log;
import game.entities.bullet.Bullet;
import game.entities.bullet.BulletManager;
import game.entities.enemies.Enemy;
import game.entities.enemies.EnemyState;
import game.entities.particles.ParticlesManager;
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
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

public class Galaga extends Application {

    private Sky sky;
    private Player player;
    private List<Enemy> enemies;
    private BulletManager bullets;
    private ParticlesManager particles;
    private int levelIndex = -1;

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
        if (args.length > 0 && args[0].equalsIgnoreCase("--debug")) {
            Application.DEBUG_MODE = true;
        }
        if (ManagementFactory.getRuntimeMXBean().
                getInputArguments().toString().indexOf("-agentlib:jdwp") > 0) {
            Application.DEBUG_MODE = true;
        }

        Galaga game = new Galaga();
        game.start();
    }

    public Galaga() {
        super(Config.WINDOW_TITLE, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        getContext().setState(new State());
    }

    private boolean loadNextLevel() {
        this.levelIndex++;
        getContext().getState().level = getContext().getResource().get(Config.LEVELS.get(this.levelIndex));

        this.player.setShooting(false);
        if(this.levelIndex > 0)
        {
            this.player.onFinishLevel();
        }

        Level level = getContext().getState().level;
        if (level == null) {
            return false;
        }
        this.enemies = level.getEnemies();
        for (Enemy enemy : this.enemies) {
            if (!enemy.init()) {
                return false;
            }
        }
        return true;
    }

    private boolean load() {
        if (!this.player.init()) {
            return false;
        }

        if(!this.loadNextLevel()) {
            return false;
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

        Sprite medal = Galaga.getContext().getResource().get(Config.SPRITE_MEDAL);
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
        rm.register(LevelResource.NAME, LevelResource.class);

        rm.add(Config.FONTS, "font", (ResourceVariant variant, Resource<?> rawRes) -> {
            if (variant != null && variant.getName().equals(Config.VARIANT_FONT_LARGE)) {
                FontResource font = (FontResource) rawRes;
                this.loadingText.setFont(font.getData());
            }
        });

        rm.add(Config.SPRITE_SHIP, "sprite");
        rm.add(Config.SPRITE_MEDAL, "sprite");
        rm.add(Config.SPRITES_ENEMY, "sprite");

        rm.add(Config.LEVELS, "level");

        this.particles = new ParticlesManager();
        this.sky = new Sky(Config.SIZE_SKY_GRID);
        if (!this.sky.init()) {
            return false;
        }

        getContext().getState().player = new Player();
        this.player = getContext().getState().player;

        getContext().getState().bullets = new BulletManager();
        this.bullets = getContext().getState().bullets;

        rm.load(() -> {
            if (!this.load()) {
                this.stop();
            }
        }, Config.SPEED_LOADING);
        return true;
    }

    @Override
    protected void update(float dt) {
        if (getContext().getInput().isKeyDown(KeyEvent.VK_ESCAPE)) {
            this.stop();
        }

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

        boolean allActionDone = true;
        boolean allInFormation = true;
        for (Enemy enemy : this.enemies) {
            enemy.update(dt);

            allActionDone = allActionDone && enemy.hasDoneAction();
            allInFormation = allInFormation && enemy.getState() == EnemyState.FORMATION;
        }

        if (allActionDone) {
            for (Enemy enemy : this.enemies) {
                enemy.resetAction();
            }
        }

        if (allInFormation && !this.player.isShootingActive()) {
            this.player.setShooting(true);
        }

        if (!this.bullets.isEmpty() && !this.enemies.isEmpty()) {
            List<Bullet> bulletsRemove = new ArrayList<>();

            for (Bullet bullet : this.bullets) {
                if (bullet.isOutOfBounds()) {
                    bulletsRemove.add(bullet);
                    continue;
                }
                bullet.update(dt);

                if (bullet.getShooter() instanceof Player) {
                    List<Enemy> enemiesRemove = new ArrayList<>();
                    for (Enemy enemy : this.enemies) {
                        if (enemy.collideWith(bullet)) {
                            enemiesRemove.add(enemy);
                            bulletsRemove.add(bullet);
                            this.player.onKillEnemy(enemy);
                            this.particles.createExplosion(enemy);
                            continue;
                        }

                        if (enemy.collideWith(this.player)) {
                            this.particles.createExplosion(this.player);
                            this.player.onKillEnemy(enemy);
                            this.player.onHit();
                            enemiesRemove.add(enemy);
                        }
                    }
                    this.enemies.removeAll(enemiesRemove);
                } else if (this.player.collideWith(bullet)) {
                    this.particles.createExplosion(this.player);
                    this.player.onHit();
                    bulletsRemove.add(bullet);
                }
            }
            this.bullets.removeAll(bulletsRemove);
        } else if (!this.enemies.isEmpty()) {
            List<Enemy> enemiesRemove = new ArrayList<>();
            for (Enemy enemy : this.enemies) {
                if (enemy.collideWith(this.player)) {
                    this.particles.createExplosion(this.player);
                    this.particles.createExplosion(enemy);
                    this.player.onHit();
                    enemiesRemove.add(enemy);
                }
            }
            this.enemies.removeAll(enemiesRemove);
        }

        if (this.enemies.isEmpty() && !this.loadNextLevel()) {
            Log.error("Failed to load next level");
            this.stop();
            return;
        }   

        if (this.player.isDead()) {
            throw new UnsupportedOperationException("TODO: Game Over handling");
        }

        this.particles.update(dt);
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
        this.particles.draw();
        this.bullets.draw();

        this.player.draw();
        for (Enemy enemy : this.enemies) {
            enemy.draw();
        }

        this.hud.draw();
        this.fud.draw();

        if (Application.DEBUG_MODE) {
            // getContext().getRenderer().drawGrid(Config.SIZE_SKY_GRID, Color.WHITE);
            // getContext().getRenderer().drawCross(Color.RED);
        }
    }

}
