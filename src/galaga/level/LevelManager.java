package galaga.level;

import engine.elements.ui.Alignment;
import engine.elements.ui.text.Text;
import engine.graphics.Renderer;
import engine.resource.sound.Sound;
import engine.utils.Position;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaSound;
import galaga.entities.enemies.Enemy;
import galaga.entities.player.Player;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

public class LevelManager {
    private final List<Enemy> pendingEnemies = new ArrayList<>();

    private List<Enemy> enemies;
    private final Player player;

    private int index = -1;
    private Level level;

    private Text title;
    private Text subtitle;
    private float titleTime;

    private Sound startSound;
    private Sound endSound;
    private Sound bestScoreSound;

    public LevelManager(Player player) {
        this.player = player;
    }

    public boolean init() {
        this.startSound = Galaga.getContext().getResource().get(GalagaSound.level_start);
        if (this.startSound == null) {
            return false;
        }

        this.endSound = Galaga.getContext().getResource().get(GalagaSound.level_end);
        if (this.endSound == null) {
            return false;
        }

        this.bestScoreSound = Galaga.getContext().getResource().get(GalagaSound.level_bestscore);
        if (this.bestScoreSound == null) {
            return false;
        }

        Font titleFont = Galaga.getContext().getResource().get(Config.FONTS, Config.VARIANT_FONT_XLARGE);
        if (titleFont == null) {
            return false;
        }

        this.title = new Text("", Position.of(
                Galaga.getContext().getApplication().getSize()).half(), Color.CYAN, titleFont);
        this.title.setCenter(Alignment.CENTER, Alignment.BEGIN);

        this.subtitle = new Text("", Position.of(
                Galaga.getContext().getApplication().getSize()).half().addY(50.f), Color.LIGHT_GRAY,
                titleFont.deriveFont(24f));
        this.subtitle.setCenter(Alignment.CENTER, Alignment.BEGIN);

        return true;
    }

    public Level getLevel() {
        return this.level;
    }

    public void spawnEnemy(Enemy enemy) {
        if (enemy == null || !enemy.init()) {
            return;
        }
        this.pendingEnemies.add(enemy);
    }

    public void flushSpawnedEnemies() {
        if (this.pendingEnemies == null || this.pendingEnemies.isEmpty()) {
            return;
        }

        this.enemies.addAll(this.pendingEnemies);
        this.pendingEnemies.clear();
    }

    public boolean next() {
        this.index++;
        if (Config.LEVELS.size() <= this.index) {
            // return this.generate();
            return false;
        }

        this.level = Galaga.getContext().getResource()
                .get(Config.LEVELS.get(this.index));
        this.player.setShooting(false);
        if (this.index > 0) {
            this.player.onFinishLevel();
        }

        if (this.level == null) {
            return false;
        }

        this.titleTime = Config.DELAY_LEVEL_TITLE;
        this.title.setText(level.getName());
        this.title.setColor(Color.CYAN);

        this.enemies = level.getEnemies();
        if (this.enemies == null || this.enemies.isEmpty()) {
            return false;
        }

        for (Enemy enemy : this.enemies) {
            if (!enemy.init()) {
                return false;
            }
        }

        this.startSound.play();
        return true;
    }

    public List<Enemy> getEnemies() {
        return this.enemies;
    }

    public void close() {
        this.startSound.stop();
        this.endSound.stop();
        this.bestScoreSound.stop();
    }

    public void updateTitle(float dt) {
        if (this.titleTime <= 0.f) {
            return;
        }
        this.titleTime -= dt;

        int alpha = (int) Math.clamp((255.f * (this.titleTime / Config.DELAY_LEVEL_TITLE)), 0.f, 255.f);
        this.title.setColor(new Color(
                this.title.getColor().getRed(),
                this.title.getColor().getGreen(),
                this.title.getColor().getBlue(),
                alpha));

        if (this.subtitle.getText().isBlank()) {
            return;
        }
        this.subtitle.setColor(new Color(
                this.subtitle.getColor().getRed(),
                this.subtitle.getColor().getGreen(),
                this.subtitle.getColor().getBlue(),
                alpha));
    }

    public boolean isTitleActive() {
        return this.titleTime > 0.f;
    }

    public void drawTitle(Renderer renderer) {
        this.title.draw(renderer);
        if (!this.subtitle.getText().isBlank()) {
            this.subtitle.draw(renderer);
        }
    }

    public void onPlayerBestScore() {
        this.bestScoreSound.play();

        this.titleTime = Config.DELAY_LEVEL_TITLE;
        this.title.setText("New Best Score!");
        this.title.setColor(Color.YELLOW);
    }

    public void onPlayerWin(int score) {
        this.bestScoreSound.play();

        this.titleTime = Config.DELAY_LEVEL_TITLE * 2.f;
        this.title.setText("Congratulations you win!");
        this.title.setColor(Color.YELLOW);

        this.subtitle.setText("Final Score: " + score);
        this.subtitle.setColor(Color.YELLOW);
    }

    public void onPlayerDied(int score) {
        this.endSound.play();

        this.titleTime = Config.DELAY_LEVEL_TITLE_DEAD;
        this.title.setText("Game Over");
        this.title.setColor(Color.RED);

        this.subtitle.setText("Final Score: " + score);
        this.subtitle.setColor(Color.RED);
    }

}
