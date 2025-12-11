package galaga.pages.game;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import engine.utils.logger.Log;
import galaga.Config;
import galaga.Galaga;
import galaga.entities.bullet.Bullet;
import galaga.entities.bullet.BulletManager;
import galaga.entities.enemies.Enemy;
import galaga.entities.enemies.EnemyState;
import galaga.entities.particles.ParticlesManager;
import galaga.entities.player.Player;
import galaga.entities.sky.Sky;
import galaga.level.Level;
import galaga.pages.GalagaPage;

import java.util.ArrayList;
import java.util.List;

public class Game extends Page<GalagaPage> {

    private Sky sky;
    private Player player;
    private List<Enemy> enemies;
    private BulletManager bullets;
    private ParticlesManager particles;
    private int levelIndex = -1;

    private FUD fud;
    private HUD hud;

    public Game() {
        super(GalagaPage.GAME);
    }

    @Override
    public boolean onActivate() {
        this.particles = new ParticlesManager();
        this.bullets = new BulletManager();

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
            return  false;
        }

        if(!this.loadNextLevel())
        {
            return false;
        }

        Galaga.getContext().getState().bullets = this.bullets;
        Galaga.getContext().getState().player = this.player;
        
        this.state = PageState.ACTIVE;
        return true;
    }

    @Override
    public boolean onDeactivate() {
        this.state = PageState.INACTIVE;
        return true;
    }

    private boolean loadNextLevel() {
        this.levelIndex++;
        Level level = Galaga.getContext().getResource()
                .get(Config.LEVELS.get(this.levelIndex));

        this.player.setShooting(false);
        if (this.levelIndex > 0) {
            this.player.onFinishLevel();
        }

        if (level == null) {
            return false;
        }
        this.enemies = level.getEnemies();
        for (Enemy enemy : this.enemies) {
            if (!enemy.init()) {
                return false;
            }
        }

        Galaga.getContext().getState().level = level;
        return true;
    }

    @Override
    public void update(float dt) {
        this.sky.update(dt);
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
            Galaga.getContext().getApplication().stop();
            return;
        }

        if (this.player.isDead()) {
            // TODO: game over screen
            Galaga.getContext().getApplication().setCurrentPage(GalagaPage.MENU);
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

        this.hud.draw();
        this.fud.draw();
    }

}
