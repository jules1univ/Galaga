package galaga.level;

import engine.Application;
import engine.utils.logger.Log;
import galaga.Config;
import galaga.entities.enemies.Enemy;
import galaga.entities.enemies.EnemyFactory;
import galaga.entities.enemies.EnemySetting;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Level {

    private final String name;
    private final float formationSpeed;
    private final float attackCooldown;
    private final float missileCooldown;

    private final List<EnemySetting> enemies = new ArrayList<>();

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

            int delayIndex = Config.DELAY_ENTER_INDEX;
            if (Application.DEBUG_MODE) {
                delayIndex = 0;
            }
            int actionEnterDelay = delayIndex;
            while (i < lines.size() && !lines.get(i).trim().isEmpty()) {
                EnemySetting enemy = EnemySetting.createEnemySetting(lines.get(i), actionEnterDelay, level);
                actionEnterDelay += delayIndex;

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
        List<Enemy> enemyInst = new ArrayList<>();
        for (EnemySetting setting : this.enemies) {
            Enemy enemy = EnemyFactory.create(setting, this.formationSpeed, this.attackCooldown, this.missileCooldown);
            if (enemy == null) {
                continue;
            }
            enemyInst.add(enemy);
        }

        enemyInst.sort((a, b) -> Float.compare(b.getLockPosition().getY(), a.getLockPosition().getY()));
        return enemyInst;
    }
}
