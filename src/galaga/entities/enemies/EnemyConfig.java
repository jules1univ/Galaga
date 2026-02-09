package galaga.entities.enemies;

import engine.graphics.sprite.Sprite;
import engine.utils.Pair;
import engine.utils.Position;
import engine.utils.logger.Log;
import galaga.Config;
import galaga.Galaga;
import galaga.resources.level.Level;

import java.util.ArrayList;
import java.util.List;

public class EnemyConfig {
    public static final int NO_INDEX = -1;

    private final Level level;

    private final EnemyType type;
    private final Position lockPosition;

    private final int scoreValue;
    private final float speed;

    private int index;

    public static EnemyConfig create(String line, int index, Level level) {
        String[] data = line.split(" ");
        if (data.length < 6) {
            Log.error("Level enemy data is invalid: %s", line);
            return null;
        }
        String enemyType = data[0];
        try {
            float lockXPercent = Float.parseFloat(data[1]);
            float width = Float.parseFloat(data[3]) / 2.f;
            float lockX = (1.f - (lockXPercent + width)) * Galaga.getContext().getFrame().getWidth();

            float lockYPercent = Float.parseFloat(data[2]);

            float lockY = (1.f - lockYPercent) * Galaga.getContext().getFrame().getHeight();

            Position lock = Position.of(lockX, lockY);

            int value = Integer.parseInt(data[4]);
            float speed = Float.parseFloat(data[5]) * Config.SPEED_ENEMY_FACTOR;

            EnemyType type = EnemyType.valueOf(enemyType.toUpperCase());
            return new EnemyConfig(type, lock, value, speed, index, level);

        } catch (NumberFormatException e) {
            Log.error("Level enemy parsing failed: %s", e.getMessage());
            return null;
        }
    }

    public static Pair<List<EnemyConfig>, Pair<Integer, Integer>> create(EnemyType type, int score, float speed, int count, float y, Level level, Pair<Integer, Integer> lrIndex) {
        List<EnemyConfig> enemies = new ArrayList<>();

        Sprite sprite = Galaga.getContext().getResource().get(type);
        if (sprite == null) {
            return Pair.of(enemies, lrIndex);
        }

        float width = sprite.getSize().getWidth() * Config.SPRITE_SCALE_DEFAULT;
        float spacing = width / 8.f;
        float x = Config.WINDOW_WIDTH / 2.f - ((count - 1) * (width + spacing)) / 2.f;

        int left = lrIndex.getFirst();
        int right = lrIndex.getSecond();
        for (int i = 0; i < count; i++) {
            Position lock = Position.of(
                    x,
                    y);
            EnemyConfig enemy = new EnemyConfig(type, lock, score, speed * Config.SPEED_ENEMY_FACTOR, 0, level);
            if (x < Config.WINDOW_WIDTH / 2.f) {
                enemy.setIndex(left);
                left++;
            } else {
                enemy.setIndex(right);
                right++;
            }
            enemies.add(enemy);

            x += width + spacing;
        }

        return Pair.of(enemies, Pair.of(left, right));
    }

    public EnemyConfig(EnemyType type, Position lock, int value, float speed, int index, Level level) {
        this.level = level;

        this.type = type;
        this.lockPosition = lock;

        this.scoreValue = value;
        this.speed = speed;

        this.index = index;
    }

    public Level getLevel() {
        return this.level;
    }

    public EnemyType getType() {
        return this.type;
    }

    public Position getLockPosition() {
        return this.lockPosition;
    }

    public int getScoreValue() {
        return this.scoreValue;
    }

    public float getSpeed() {
        return this.speed;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
