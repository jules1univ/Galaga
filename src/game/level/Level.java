package game.level;

import engine.utils.Position;
import engine.utils.logger.Log;
import game.Config;
import game.Galaga;
import game.entities.enemies.Enemy;
import game.entities.enemies.EnemyBee;
import game.entities.enemies.EnemyButterFly;
import game.entities.enemies.EnemyMoth;
import game.entities.enemies.EnemyType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Level {

    private final String name;
    private final float formationSpeed;
    private final float attackCooldown;
    private final float missileCooldown;

    private final List<Enemy> enemies = new LinkedList<>();

    public static Level createLevel(InputStream in) {

        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            Log.error("Level loading failed: " + e.getMessage());
            return null;
        }

        for (int i = 0; i < lines.size();) {
            Level level = createLevelFromHeader(lines.get(i));
            if (level == null) {
                return null;
            }

            i++;
            int actionIndex = 0;
            while (i < lines.size() && !lines.get(i).trim().isEmpty()) {
                Enemy enemy = createEnemyFromLine(lines.get(i), actionIndex, level);
                if (!(enemy.getType() == EnemyType.MOTH && level.getAttackCooldown() <= 0.f)) {
                    actionIndex++;
                }
                level.enemies.add(enemy);
                i++;
            }
            return level;
        }

        return null;
    }

    private static Level createLevelFromHeader(String lineHeader) {
        String[] header = lineHeader.split(" ");
        if (header.length < 4) {
            Log.error("Level header is invalid or level already exists: " + lineHeader);
            return null;
        }

        try {
            float formationSpeed = Float.parseFloat(header[1]);
            float attackCooldown = Integer.parseInt(header[2]) * Config.DELAY_ENEMY_COOLDOWN_FACTOR_ATTACK;
            float missileCooldown = Integer.parseInt(header[3]) * Config.DELAY_ENEMY_COOLDOWN_FACTOR_MISSILE;
            return new Level(header[0], formationSpeed, attackCooldown, missileCooldown);
        } catch (NumberFormatException e) {
            Log.error("Level header parsing failed: " + e.getMessage());
            return null;
        }
    }

    private static Enemy createEnemyFromLine(String line, int index, Level level) {
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
            switch (type) {
                case EnemyType.BEE -> {
                    return new EnemyBee(lock, index, value, speed, level.getFormationSpeed(), level.getMissileCooldown());
                }
                case EnemyType.BUTTERFLY -> {
                    return new EnemyButterFly(lock, index, value, speed, level.getFormationSpeed(), level.getMissileCooldown());
                }
                case EnemyType.MOTH -> {
                    return new EnemyMoth(lock, index, value, speed, level.getFormationSpeed(), level.getAttackCooldown());
                }
                default -> {
                    Log.error("Unknown enemy type: " + enemyType);
                    return null;
                }
            }
        } catch (NumberFormatException e) {
            Log.error("Level enemy parsing failed: " + e.getMessage());
            return null;
        }
    }

    private Level(String name, float formationSpeed, float attackCooldown,
            float missileCooldown) {
        this.name = name;
        this.formationSpeed = formationSpeed;
        this.attackCooldown = attackCooldown;
        this.missileCooldown = missileCooldown;
    }

    public String getName() {
        return name;
    }

    public float getFormationSpeed() {
        return formationSpeed;
    }

    public float getAttackCooldown() {
        return attackCooldown;
    }

    public float getMissileCooldown() {
        return missileCooldown;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }
}
