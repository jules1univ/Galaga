package galaga.entities.enemies;

public class EnemyFactory {
    public static Enemy create(EnemySetting setting, float formationSpeed, float attackCooldown,
            float missileCooldown) {

        switch (setting.getType()) {
            case BEE -> {
                return new EnemyBee(setting, formationSpeed, missileCooldown);
            }
            case BUTTERFLY -> {
                return new EnemyButterFly(setting, formationSpeed, missileCooldown);
            }
            case MOTH -> {
                return new EnemyMoth(setting, formationSpeed, attackCooldown);
            }
            case BOSCONIAN -> throw new UnsupportedOperationException("Unimplemented case: " + setting.getType());
            case DRAGONFLY -> throw new UnsupportedOperationException("Unimplemented case: " + setting.getType());
            case GALAXIAN -> throw new UnsupportedOperationException("Unimplemented case: " + setting.getType());
            case SATELLITE -> throw new UnsupportedOperationException("Unimplemented case: " + setting.getType());
            case SCORPION -> throw new UnsupportedOperationException("Unimplemented case: " + setting.getType());
            case STARSHIP -> throw new UnsupportedOperationException("Unimplemented case: " + setting.getType());
            default -> {
                return null;
            }
        }
    }
}