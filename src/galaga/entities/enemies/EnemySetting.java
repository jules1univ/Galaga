package galaga.entities.enemies;

import engine.utils.Position;

public class EnemySetting {
    private final EnemyType type;
    private final Position lockPosition;

    private final int actionIndex;
    private final int enterIndex;

    private final int scoreValue;
    private final float speed;

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
