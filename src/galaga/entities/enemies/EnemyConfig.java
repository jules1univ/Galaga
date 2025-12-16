package galaga.entities.enemies;

import engine.utils.Position;
import engine.utils.logger.Log;
import galaga.Config;
import galaga.Galaga;
import galaga.level.Level;

public class EnemyConfig {
    private final Level level;

    private final EnemyType type;
    private final Position lockPosition;

    private final int scoreValue;
    private final float speed;

    public static EnemyConfig create(String line, Level level) {
        String[] data = line.split(" ");
        if (data.length < 6) {
            Log.error("Level enemy data is invalid: " + line);
            return null;
        }
        String enemyType = data[0];
        try {
            float lockXPercent = Float.parseFloat(data[1]);
            float lockX = (1.f - lockXPercent) * Galaga.getContext().getFrame().getWidth();

            float lockYPercent = Float.parseFloat(data[2]);
            float lockY = (1.f - lockYPercent) * Galaga.getContext().getFrame().getHeight();

            Position lock = Position.of(lockX, lockY);

            // we no longer use size for enemies => sprite have their own fixed size
            // float size = Float.parseFloat(data[3]);
            int value = Integer.parseInt(data[4]);
            float speed = Float.parseFloat(data[5]) * Config.SPEED_ENEMY_FACTOR;

            EnemyType type = EnemyType.valueOf(enemyType.toUpperCase());
            return new EnemyConfig(type, lock, value, speed, level);

        } catch (NumberFormatException e) {
            Log.error("Level enemy parsing failed: " + e.getMessage());
            return null;
        }
    }

    public EnemyConfig(EnemyType type, Position lock, int value, float speed, Level level) {
        this.level = level;

        this.type = type;
        this.lockPosition = lock;

        this.scoreValue = value;
        this.speed = speed;
    }

    public Level getLevel() {
        return level;
    }

    public EnemyType getType() {
        return type;
    }

    public Position getLockPosition() {
        return lockPosition;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public float getSpeed() {
        return speed;
    }

}
