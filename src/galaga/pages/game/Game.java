package galaga.pages.game;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import engine.resource.sound.Sound;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaSound;
import galaga.entities.bullet.Bullet;
import galaga.entities.bullet.BulletManager;
import galaga.entities.enemies.Enemy;
import galaga.entities.enemies.EnemyMoth;
import galaga.entities.enemies.EnemyState;
import galaga.entities.particles.ParticlesManager;
import galaga.entities.player.Player;
import galaga.entities.sky.Sky;
import galaga.level.LevelManager;
import galaga.pages.GalagaPage;
import galaga.score.Score;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Game extends Page<GalagaPage> {

    private Sky sky;
    private Player player;
    private List<Enemy> enemies = new ArrayList<>();

    private BulletManager bullets;
    private LevelManager level;
    private ParticlesManager particles;

    private FUD fud;
    private HUD hud;

    private Sound themeSound;

    private boolean bestScoreUpdated = false;
    private int bestScore = 0;

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

        this.level = new LevelManager(this.player);
        if (!this.level.init() || !this.level.next()) {
            return false;
        }
        this.enemies = this.level.getEnemies();

        this.fud = new FUD();
        if (!this.fud.init()) {
            return false;
        }

        this.hud = new HUD();
        if (!this.hud.init()) {
            return false;
        }

        Score playerBestScore = Galaga.getContext().getResource().get(Config.BEST_SCORE);
        if (playerBestScore != null) {
            this.bestScore = playerBestScore.getValue();
        }

        Galaga.getContext().getState().particles = this.particles;
        Galaga.getContext().getState().bullets = this.bullets;
        Galaga.getContext().getState().player = this.player;
        Galaga.getContext().getState().level = this.level;

        this.state = PageState.ACTIVE;
        return true;
    }

    @Override
    public boolean onDeactivate() {
        this.themeSound.stop();
        this.level.close();

        Score score = new Score(this.player.getScore());
        Score bestScore = Galaga.getContext().getResource().get(Config.BEST_SCORE);
        if (score.compareTo(bestScore) > 0) {
            Galaga.getContext().getResource().write(Config.BEST_SCORE, score);
        }

        this.state = PageState.INACTIVE;
        return true;
    }

    private void handleCollisions() {
        Iterator<Enemy> enemyIt = this.enemies.iterator();
        while (enemyIt.hasNext()) {
            Enemy enemy = enemyIt.next();

            if (enemy.collideWith(this.player)) {
                if (enemy instanceof EnemyMoth enemyMoth) {
                    enemyMoth.capture(this.player);
                    continue;
                }

                this.player.onCollideWithEnemy(enemy);
                enemy.onCollideWithPlayer();
                enemyIt.remove();
                continue;
            }

            Iterator<Bullet> bulletIt = this.bullets.iterator();
            while (bulletIt.hasNext()) {
                Bullet bullet = bulletIt.next();
                if (bullet.isOutOfBounds()) {
                    bulletIt.remove();
                    continue;
                }

                if (bullet.getShooter() instanceof Player && enemy.isBulletColliding(bullet)) {
                    this.player.onBulletHitOther(enemy);
                    enemyIt.remove();
                    bulletIt.remove();
                    break;
                }
            }
        }

        Iterator<Bullet> bulletIt = this.bullets.iterator();
        while (bulletIt.hasNext()) {
            Bullet bullet = bulletIt.next();
            if (bullet.getShooter() instanceof Enemy && this.player.isBulletColliding(bullet)) {
                bullet.getShooter().onBulletHitOther(this.player);
                if (this.player.isDead()) {
                    break;
                }
                bulletIt.remove();
            }
        }
    }

    @Override
    public void update(float dt) {
        if (this.player.getScore() > this.bestScore && !this.bestScoreUpdated) {
            this.level.onNewBestScore();
            this.sky.setColor(Color.ORANGE);
            this.bestScoreUpdated = true;
        }

        this.sky.update(dt);
        for (Bullet bullet : bullets) {
            bullet.update(dt);
        }

        this.player.update(dt);

        boolean allActionDone = true;
        boolean allInFormation = true;
        for (Enemy enemy : enemies) {
            enemy.update(dt);
            allActionDone = allActionDone && enemy.hasDoneAction();
            allInFormation = allInFormation && enemy.getState() == EnemyState.FORMATION;
        }

        if (allActionDone && allInFormation && !this.enemies.isEmpty()) {
            for (Enemy enemy : this.enemies) {
                if (enemy.canPerformAction()) {
                    enemy.resetAction();
                    break;
                }
            }
        }

        if (allInFormation && !this.player.isShootingActive()) {
            this.player.setShooting(true);
        }

        this.handleCollisions();

        if (this.player.isDead()) {
            Galaga.getContext().getApplication().setCurrentPage(GalagaPage.MENU);
        }
        if (this.enemies.isEmpty()) {
            if (this.level.next()) {
                this.player.setShooting(false);
                this.enemies = this.level.getEnemies();
            } else {
                Galaga.getContext().getApplication().setCurrentPage(GalagaPage.MENU);
            }
        }

        this.level.updateTitle(dt);

        if (this.level.isTitleActive()) {
            this.level.updateTitle(dt);
        } else if (this.sky.isActiveColor()) {
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

        if (this.level.isTitleActive()) {
            this.level.drawTitle();
        }

        this.hud.draw();
        this.fud.draw();
    }

}
