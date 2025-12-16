package galaga.entities.enemies;

import engine.utils.Position;
import galaga.Config;
import galaga.entities.enemies.types.EnemyBee;
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
            default -> {
                return null;
            }
        }
    }

    public static EnemyCapturedPlayer createCapturedPlayer(EnemyConfig baseConfig) {
        
        Position lockOutside = Position.of(Config.WINDOW_WIDTH / 2.f, -100.f);
        EnemyConfig config = new EnemyConfig(EnemyType.CAPTURED_PLAYER, lockOutside, baseConfig.getScoreValue(), baseConfig.getSpeed(), baseConfig.getLevel());

        EnemyCapturedPlayer capturedPlayer = new EnemyCapturedPlayer(config);
        return capturedPlayer;
    }
}