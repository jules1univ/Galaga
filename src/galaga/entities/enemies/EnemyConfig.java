package galaga.entities.enemies;

import java.util.ArrayList;
import java.util.List;

import engine.graphics.sprite.Sprite;
import engine.utils.Position;
import engine.utils.logger.Log;
import galaga.Config;
import galaga.Galaga;
import galaga.level.Level;

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
            Log.error("Level enemy data is invalid: " + line);
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
            Log.error("Level enemy parsing failed: " + e.getMessage());
            return null;
        }
    }

    public static List<EnemyConfig> create(EnemyType type, int score, float speed, int count, float y, Level level) {
        List<EnemyConfig> enemies = new ArrayList<>();

        Sprite sprite = Galaga.getContext().getResource().get(type);
        if(sprite == null) {
            return enemies;
        }


        float width = sprite.getSize().getWidth() * Config.SPRITE_SCALE_DEFAULT;
        float spacing = width/8.f;
        float x = Config.WINDOW_WIDTH / 2.f - ((count - 1) * (width + spacing)) / 2.f;

        for (int i = 0; i < count; i++) {
            Position lock = Position.of(
                x,
                y
            );
            EnemyConfig config = new EnemyConfig(type, lock, score, speed * Config.SPEED_ENEMY_FACTOR, 0, level);
            enemies.add(config);

            x += width + spacing;
        }

        return enemies;
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
