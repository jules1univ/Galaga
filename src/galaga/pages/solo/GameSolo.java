package galaga.pages.solo;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import engine.graphics.Renderer;
import engine.resource.sound.Sound;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaPage;
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
import galaga.score.Score;
import java.awt.Color;
import java.util.Iterator;

public class GameSolo extends Page<GalagaPage> {

    private Sky sky;
    private Player player;

    private BulletManager bullets;
    private LevelManager level;
    private ParticlesManager particles;

    private GameFooterDisplay gfd;
    private GameHeaderDisplay ghd;

    private Sound themeSound;

    private boolean playerDiedUpdated = false;
    private boolean bestScoreUpdated = false;
    private int bestScore = 0;

    public GameSolo() {
        super(GalagaPage.SOLO_GAME);
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

        this.gfd = new GameFooterDisplay();
        if (!this.gfd.init()) {
            return false;
        }

        this.ghd = new GameHeaderDisplay();
        if (!this.ghd.init()) {
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
        Score bestScoreVal = Galaga.getContext().getResource().get(Config.BEST_SCORE);
        if (score.compareTo(bestScoreVal) > 0) {
            Galaga.getContext().getResource().write(Config.BEST_SCORE, score);
        }

        this.state = PageState.INACTIVE;
        return true;
    }

    @Override
    public void onReceiveArgs(Object... args) {
    }

    @Override
    public void update(float dt) {
        if (this.player.getScore() > this.bestScore && !this.bestScoreUpdated) {
            this.level.onNewBestScore();
            this.sky.setColor(Color.ORANGE);
            this.bestScoreUpdated = true;
        }

        if (this.level.isTitleActive()) {
            this.level.updateTitle(dt);
        } else if (this.sky.isActiveColor()) {
            if (this.playerDiedUpdated) {
                Galaga.getContext().getApplication().setCurrentPage(GalagaPage.MAIN_MENU);
                return;
            }
            this.sky.restoreColor();
        }

        this.sky.update(dt);
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

            if (enemy.collideWith(player)) {
                if (enemy instanceof EnemyMoth enemyMoth) {
                    enemyMoth.capture(player);
                    continue;
                }

                player.onCollideWithEnemy(enemy);
                enemy.onCollideWithPlayer();
                enemyIt.remove();
            }

        }

        if (allActionDone && allInFormation && actionEnemy != null && !this.player.isReswawning()) {
            actionEnemy.resetAction();
        }

        if (allInFormation && !this.player.isShootingActive()) {
            this.player.setShooting(true);
        }

        Iterator<Bullet> bulletIt = bullets.iterator();
        while (bulletIt.hasNext()) {
            Bullet bullet = bulletIt.next();
            bullet.update(dt);

            if (bullet.isOutOfBounds()) {
                bulletIt.remove();
                continue;
            }

            if (!this.player.isReswawning() && bullet.getShooter() instanceof Enemy && this.player.isBulletColliding(bullet)) {
                bullet.getShooter().onBulletHitOther(this.player);
                bulletIt.remove();

                if (this.player.isDead()) {
                    break;
                }
                continue;
            }

            enemyIt = this.level.getEnemies().iterator();
            while (enemyIt.hasNext()) {
                Enemy enemy = enemyIt.next();
                if (bullet.getShooter() instanceof Player && enemy.isBulletColliding(bullet)) {
                    this.player.onBulletHitOther(enemy);
                    enemyIt.remove();
                    bulletIt.remove();
                    break;
                }
            }

        }
        this.level.flushSpawnedEnemies();

        if (this.player.isDead() && !this.playerDiedUpdated) {
            this.level.onPlayerDied();
            this.sky.setColor(Color.RED);

            this.playerDiedUpdated = true;
        }

        if (this.level.getEnemies().isEmpty()) {
            if (this.level.next()) {
                this.player.setShooting(false);
            } else {
                Galaga.getContext().getApplication().setCurrentPage(GalagaPage.MAIN_MENU);
            }
        }

        this.particles.update(dt);
        this.ghd.update(dt);
        this.gfd.update(dt);
    }

    @Override
    public void draw(Renderer renderer) {
        this.sky.draw(renderer);

        this.particles.draw(renderer);

        if (!this.playerDiedUpdated) {
            this.bullets.draw(renderer);
            this.player.draw(renderer);
            for (Enemy enemy : this.level.getEnemies()) {
                enemy.draw(renderer);
            }
        }

        if (this.level.isTitleActive()) {
            this.level.drawTitle(renderer);
        }

        this.ghd.draw(renderer);
        this.gfd.draw(renderer);
    }

}
