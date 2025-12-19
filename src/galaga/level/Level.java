package galaga.level;

import engine.utils.ini.Ini;
import engine.utils.logger.Log;
import galaga.Config;
import galaga.entities.enemies.Enemy;
import galaga.entities.enemies.EnemyFactory;
import galaga.entities.enemies.EnemyConfig;
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

    private final List<EnemyConfig> enemiesConfig = new ArrayList<>();

    public static Level create(InputStream in) {

        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                lines.add(line);
            }
        } catch (IOException e) {
            Log.error("Level loading failed: " + e.getMessage());
            return null;
        }

        int version = getVersion(lines);
        if (version == 1) {
            return createVersion1(lines);
        }else if (version == 2) {
            return createVersion2(lines);
        }

        return null;
    }

    private static int getVersion(List<String> lines) {
        if (lines.isEmpty() || lines.size() < 1) {
            return -1;
        }

        String header = lines.get(0);
        if (header.toLowerCase().startsWith("[config]")) {
            return 2;
        }
        return 1;
    }

    private static Level createVersion1(List<String> lines) {
        for (int i = 0; i < lines.size();) {
            Level level = createLevelFromHeader(lines.get(i));
            if (level == null) {
                return null;
            }

            i++;
            while (i < lines.size() && !lines.get(i).trim().isEmpty()) {
                EnemyConfig enemy = EnemyConfig.create(lines.get(i), level);
                level.enemiesConfig.add(enemy);
                i++;
            }
            return level;
        }
        return null;
    }

    private static Level createVersion2(List<String> lines) {
        Ini levelConfig = Ini.load(lines);
        if (levelConfig == null) {
            return null;
        }
        
        if(!levelConfig.containsSection("config")) {
            return null;
        }

        // TODO: implement version 2
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

    public List<Enemy> getEnemiesConfig() {
        List<Enemy> enemyInst = new ArrayList<>();
        for (EnemyConfig config : this.enemiesConfig) {
            Enemy enemy = EnemyFactory.create(config);
            if (enemy == null) {
                continue;
            }
            enemyInst.add(enemy);
        }

        enemyInst.sort((a, b) -> Float.compare(b.getLockPosition().getY(), a.getLockPosition().getY()));
        return enemyInst;
    }
}
