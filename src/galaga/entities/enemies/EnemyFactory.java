package galaga.entities.enemies;

import engine.utils.Position;
import galaga.entities.enemies.types.EnemyBee;
import galaga.entities.enemies.types.EnemyBosconian;
import galaga.entities.enemies.types.EnemyButterFly;
import galaga.entities.enemies.types.EnemyCapturedPlayer;
import galaga.entities.enemies.types.EnemyMoth;

public class EnemyFactory {
    public static Enemy create(EnemyConfig config) {

        switch (config.getType()) {
            case BEE -> {
                return new EnemyBee(config);
            }
            case BUTTERFLY -> {
                return new EnemyButterFly(config);
            }
            case MOTH -> {
                return new EnemyMoth(config);
            }
            case BOSCONIAN -> {
                return new EnemyBosconian(config);
            }
            default -> {
                return null;
            }
        }
    }

    public static EnemyCapturedPlayer createCapturedPlayer(EnemyConfig baseConfig, Position capturePosition) {

        EnemyConfig config = new EnemyConfig(EnemyType.CAPTURED_PLAYER, capturePosition, baseConfig.getScoreValue(),
                baseConfig.getSpeed(), baseConfig.getLevel());

        return new EnemyCapturedPlayer(config);
    }
}