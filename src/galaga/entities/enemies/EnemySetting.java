package galaga.entities.enemies;

import java.util.Map;

import engine.utils.Position;
import engine.utils.logger.Log;
import galaga.Config;
import galaga.Galaga;
import galaga.level.Level;

public class EnemySetting {
    private final EnemyType type;
    private final Position lockPosition;

    private final int actionIndex;
    private final int enterIndex;

    private final int scoreValue;
    private final float speed;


    public static EnemySetting createEnemySetting(String line, Map<EnemyType, Integer> enemyActionIndex, int enterIndex, Level level) {
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
            enemyActionIndex.putIfAbsent(type, Config.DELAY_ACTION_INDEX);
            int actionIndex = enemyActionIndex.get(type);

            if (!(type == EnemyType.MOTH && level.getAttackCooldown() <= 0.f)) {
                enemyActionIndex.put(type, enemyActionIndex.get(type) + 1);
            }

            return new EnemySetting(type, lock, actionIndex, enterIndex, value, speed);
        } catch (NumberFormatException e) {
            Log.error("Level enemy parsing failed: " + e.getMessage());
            return null;
        }
    }

    public EnemySetting(EnemyType type, Position lock, int actionIndex, int enterIndex, int value, float speed) {
        this.type = type;
        this.lockPosition = lock;
        this.actionIndex = actionIndex;
        this.enterIndex = enterIndex;
        this.scoreValue = value;
        this.speed = speed;
    }

    public EnemyType getType() {
        return type;
    }

    public Position getLockPosition() {
        return lockPosition;
    }

    public int getActionIndex() {
        return actionIndex;
    }

    public int getEnterIndex() {
        return enterIndex;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public float getSpeed() {
        return speed;
    }

}
