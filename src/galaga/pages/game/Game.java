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
import galaga.entities.enemies.EnemyState;
import galaga.entities.enemies.EnemyType;
import galaga.entities.enemies.types.EnemyMoth;
import galaga.entities.particles.ParticlesManager;
import galaga.entities.player.Player;
import galaga.entities.sky.Sky;
import galaga.level.LevelManager;
import galaga.pages.GalagaPage;
import galaga.score.Score;

import java.awt.Color;
import java.util.Iterator;

public class Game extends Page<GalagaPage> {

    private Sky sky;
    private Player player;

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
        Iterator<Enemy> enemyIt = this.level.getEnemies().iterator();
        while (enemyIt.hasNext()) {
            Enemy enemy = enemyIt.next();

            if (!enemy.collideWith(player)) {
                continue;
            }

            if (enemy instanceof EnemyMoth enemyMoth) {
                enemyMoth.capture(player);
                continue;
            }

            player.onCollideWithEnemy(enemy);
            enemy.onCollideWithPlayer();
            enemyIt.remove();
        }

        Iterator<Bullet> bulletIt = bullets.iterator();
        while (bulletIt.hasNext()) {
            Bullet bullet = bulletIt.next();

            if (bullet.getShooter() instanceof Enemy) {
                if (player.isBulletColliding(bullet)) {
                    bullet.getShooter().onBulletHitOther(player);
                    bulletIt.remove();

                    if (player.isDead()) {
                        return;
                    }
                }
                continue;
            }

            enemyIt = this.level.getEnemies().iterator();
            while (enemyIt.hasNext()) {
                Enemy enemy = enemyIt.next();
                if (enemy.isBulletColliding(bullet)) {
                    player.onBulletHitOther(enemy);
                    enemyIt.remove();
                    bulletIt.remove();
                    break;
                }
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

        Iterator<Bullet> bulletIt = bullets.iterator();
        while (bulletIt.hasNext()) {
            Bullet bullet = bulletIt.next();
            bullet.update(dt);

            if (bullet.isOutOfBounds()) {
                bulletIt.remove();
            }
        }

        this.player.update(dt);

        boolean allActionDone = true;
        boolean allInFormation = true;
        Enemy actionEnemy = null;

        Iterator<Enemy> enemyIt = this.level.getEnemies().iterator();
        while (enemyIt.hasNext()) {
            Enemy enemy = enemyIt.next();
            enemy.update(dt);

            if (enemy.canRemove()) {
                enemy.onCollideWithPlayer();
                enemyIt.remove();
                continue;
            }

            allActionDone &= enemy.hasDoneAction();

            boolean inFormation = enemy.getState() == EnemyState.FORMATION;
            allInFormation &= inFormation;

            if ((enemy.getType() == EnemyType.CAPTURED_PLAYER)
                    || (actionEnemy == null && inFormation && enemy.canPerformAction())) {
                actionEnemy = enemy;
            }
        }

        if (allActionDone && allInFormation && actionEnemy != null && !this.player.isReswawning()) {
            actionEnemy.resetAction();
        }

        if (allInFormation && !this.player.isShootingActive()) {
            this.player.setShooting(true);
        }

        this.handleCollisions();
        this.level.flushSpawnedEnemies();

        if (this.player.isDead()) {
            Galaga.getContext().getApplication().setCurrentPage(GalagaPage.MENU);
        }

        if (this.level.getEnemies().isEmpty()) {
            if (this.level.next()) {
                this.player.setShooting(false);
            } else {
                Galaga.getContext().getApplication().setCurrentPage(GalagaPage.MENU);
            }
        }

        if (this.level.isTitleActive()) {
            this.level.updateTitle(dt);
        } else if (this.sky.isActiveColor()) {
            this.sky.restoreColor();
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
        for (Enemy enemy : this.level.getEnemies()) {
            enemy.draw();
        }

        if (this.level.isTitleActive()) {
            this.level.drawTitle();
        }

        this.hud.draw();
        this.fud.draw();
    }

}
