package galaga.pages.game;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import engine.elements.ui.text.Text;
import engine.elements.ui.text.TextPosition;
import engine.resource.sound.Sound;
import engine.utils.Position;
import engine.utils.logger.Log;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaSound;
import galaga.entities.bullet.Bullet;
import galaga.entities.bullet.BulletManager;
import galaga.entities.enemies.Enemy;
import galaga.entities.enemies.EnemyState;
import galaga.entities.particles.ParticlesManager;
import galaga.entities.player.Player;
import galaga.entities.sky.Sky;
import galaga.level.Level;
import galaga.pages.GalagaPage;
import galaga.score.Score;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

public class Game extends Page<GalagaPage> {

    private Sky sky;
    private Player player;

    private final List<Enemy> enemiesRemove = new ArrayList<>();
    private List<Enemy> enemies;

    private final List<Bullet> bulletsRemove = new ArrayList<>();
    private BulletManager bullets;

    private ParticlesManager particles;

    private int levelIndex = -1;
    private float levelTitleTime = 0.f;
    private Text levelTitle;

    private FUD fud;
    private HUD hud;

    private Sound themeSound;
    private Sound levelStart;
    private Sound levelEnd;

    private boolean bestScorePlayed = false;
    private int bestScore = 0;
    private Sound levelBestScore;

    public Game() {
        super(GalagaPage.GAME);
    }

    @Override
    public boolean onActivate() {
        if (Galaga.getContext().getState().shipSkin == null) {
            Galaga.getContext().getState().shipSkin = Galaga.getContext().getResource().get(Config.SPRITES_SHIP.get(0));
        }

        this.themeSound = Galaga.getContext().getResource().get(GalagaSound.game_theme);
        if (this.themeSound == null) {
            return false;
        }
        this.themeSound.setLoop(true);
        this.themeSound.play(0.2f);

        this.levelStart = Galaga.getContext().getResource().get(GalagaSound.level_start);
        if (this.levelStart == null) {
            return false;
        }
        this.levelEnd = Galaga.getContext().getResource().get(GalagaSound.level_end);
        if (this.levelEnd == null) {
            return false;
        }

        this.levelBestScore = Galaga.getContext().getResource().get(GalagaSound.level_bestscore);
        if (this.levelBestScore == null) {
            return false;
        }

        this.particles = new ParticlesManager();

        this.bullets = new BulletManager();
        if (!this.bullets.init()) {
            return false;
        }

        this.sky = new Sky(Config.SIZE_SKY_GRID);
        if (!this.sky.init()) {
            return false;
        }

        this.player = new Player();
        if (!this.player.init()) {
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

        Font titleFont = Galaga.getContext().getResource().get(Config.FONTS, Config.VARIANT_FONT_XLARGE);
        this.levelTitle = new Text("", Position.of(
                Galaga.getContext().getApplication().getSize()).half(), Color.CYAN, titleFont);
        this.levelTitle.setCenter(TextPosition.CENTER, TextPosition.BEGIN);

        if (!this.loadNextLevel()) {
            return false;
        }

        Score playerBestScore = Galaga.getContext().getResource().get(Config.BEST_SCORE);
        if (playerBestScore != null) {
            this.bestScore = playerBestScore.getValue();
        }

        Galaga.getContext().getState().bullets = this.bullets;
        Galaga.getContext().getState().player = this.player;

        this.state = PageState.ACTIVE;
        return true;
    }

    @Override
    public boolean onDeactivate() {
        this.themeSound.stop();
        this.levelStart.stop();
        this.levelEnd.stop();
        this.levelBestScore.stop();

        Score score = new Score(this.player.getScore());
        Score bestScore = Galaga.getContext().getResource().get(Config.BEST_SCORE);
        if (score.compareTo(bestScore) > 0) {
            Galaga.getContext().getResource().write(Config.BEST_SCORE, score);
        }

        this.state = PageState.INACTIVE;
        return true;
    }

    private boolean loadNextLevel() {
        this.levelIndex++;
        if (Config.LEVELS.size() <= this.levelIndex) {
            // TODO procedural level generation
            return false;
        }
        Level level = Galaga.getContext().getResource()
                .get(Config.LEVELS.get(this.levelIndex));

        this.player.setShooting(false);
        if (this.levelIndex > 0) {
            this.player.onFinishLevel();
        }

        if (level == null) {
            return false;
        }

        this.levelTitleTime = Config.DELAY_LEVEL_TITLE;
        this.levelTitle.setText(level.getName());
        this.levelTitle.setColor(Color.CYAN);

        this.enemies = level.getEnemies();
        if (this.enemies == null || this.enemies.isEmpty()) {
            return false;
        }

        for (Enemy enemy : this.enemies) {
            if (!enemy.init()) {
                return false;
            }
        }

        Galaga.getContext().getState().level = level;
        this.levelStart.play();
        return true;
    }

    @Override
    public void update(float dt) {
        if (this.player.getScore() > this.bestScore && !this.bestScorePlayed) {
            this.levelBestScore.play();
            this.bestScorePlayed = true;


            this.levelTitleTime = Config.DELAY_LEVEL_TITLE;

            this.levelTitle.setText("New Best Score!");
            this.levelTitle.setColor(Color.YELLOW);
            this.sky.setColor(Color.ORANGE);
        }

        this.sky.update(dt);
        this.player.update(dt);

        boolean allActionDone = true;
        boolean allInFormation = true;
        for (Enemy enemy : this.enemies) {
            enemy.update(dt);

            allActionDone = allActionDone && enemy.hasDoneAction();
            allInFormation = allInFormation && enemy.getState() == EnemyState.FORMATION;
        }

        if (allActionDone && allInFormation && !this.enemies.isEmpty()) {
            this.enemies.stream().filter(enemy -> enemy.canPerformAction()).findFirst()
                    .ifPresent(enemy -> enemy.resetAction());
        }

        if (allInFormation && !this.player.isShootingActive()) {
            this.player.setShooting(true);
        }

        if (!this.bullets.isEmpty() && !this.enemies.isEmpty()) {

            for (Bullet bullet : this.bullets) {
                if (bullet.isOutOfBounds()) {
                    bulletsRemove.add(bullet);
                    continue;
                }
                bullet.update(dt);

                if (bullet.getShooter() instanceof Player) {
                    for (Enemy enemy : this.enemies) {
                        if (enemy.collideWith(bullet)) {
                            this.particles.createExplosion(enemy);

                            this.player.onKillEnemy(enemy);
                            enemy.onDie();
                            enemiesRemove.add(enemy);
                            bulletsRemove.add(bullet);

                            continue;
                        }

                        if (enemy.collideWith(this.player)) {
                            this.particles.createExplosion(this.player);
                            this.player.onKillEnemy(enemy);
                            this.player.onHit();

                            enemy.onDie();
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
            this.bulletsRemove.clear();
        } else if (!this.enemies.isEmpty() && !allInFormation) {
            for (Enemy enemy : this.enemies) {
                if (enemy.getState() == EnemyState.FORMATION) {
                    continue;
                }

                if (enemy.collideWith(this.player)) {
                    this.particles.createExplosion(this.player);
                    this.particles.createExplosion(enemy);

                    this.player.onHit();
                    enemy.onDie();
                    enemiesRemove.add(enemy);
                }
            }

            this.enemies.removeAll(enemiesRemove);
            this.enemiesRemove.clear();
        }

        if (this.enemies.isEmpty() && !this.loadNextLevel()) {
            Log.error("Failed to load next level");
            Galaga.getContext().getApplication().stop();
            return;
        }

        if (this.player.isDead()) {
            // TODO: game over screen
            Galaga.getContext().getApplication().setCurrentPage(GalagaPage.MENU);
        }

        if (this.levelTitleTime > 0.f) {
            this.levelTitleTime -= dt;
            this.levelTitle.setColor(new Color(
                    this.levelTitle.getColor().getRed(),
                    this.levelTitle.getColor().getGreen(),
                    this.levelTitle.getColor().getBlue(),
                    (int) (255.f * (this.levelTitleTime / Config.DELAY_LEVEL_TITLE))));
        }else if(this.sky.isActiveColor()) {
            this.sky.restorColor();
        }

        this.particles.update(dt);
        this.hud.update(dt);
        this.fud.update(dt);
    }

    @Override
    public void draw() {
        this.sky.draw();

        this.particles.draw();
        this.bullets.draw();

        this.player.draw();
        for (Enemy enemy : this.enemies) {
            enemy.draw();
        }

        if (this.levelTitleTime > 0.f) {
            this.levelTitle.draw();
        }

        this.hud.draw();
        this.fud.draw();
    }

}
