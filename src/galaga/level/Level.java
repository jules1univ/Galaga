package galaga.level;

import engine.graphics.sprite.Sprite;
import engine.utils.Pair;
import engine.utils.ini.Ini;
import engine.utils.logger.Log;
import galaga.Config;
import galaga.Galaga;
import galaga.entities.enemies.Enemy;
import galaga.entities.enemies.EnemyConfig;
import galaga.entities.enemies.EnemyFactory;
import galaga.entities.enemies.EnemyType;
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
            Log.error("Level loading failed: %s", e.getMessage());
            return null;
        }

        int version = getVersion(lines);
        if (version == 1) {
            return createVersion1(lines);
        } else if (version == 2) {
            return createVersion2(lines);
        }

        return null;
    }

    private static int getVersion(List<String> lines) {
        if (lines.isEmpty() || lines.size() < 1) {
            return -1;
        }

        for (String line : lines) {
            if (line.trim().startsWith("[level]")) {
                return 2;
            }
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

            int left = 0;
            int right = 0;
            while (i < lines.size() && !lines.get(i).trim().isEmpty()) {
                EnemyConfig enemy = EnemyConfig.create(lines.get(i), EnemyConfig.NO_INDEX, level);

                float x = enemy.getLockPosition().getX();
                if (x < Config.WINDOW_WIDTH / 2.f) {
                    enemy.setIndex(left);
                    left++;
                } else {
                    enemy.setIndex(right);
                    right++;
                }

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

        if (!levelConfig.hasSection("level")) {
            return null;
        }

        try {
            String name = levelConfig.getVariable("level", "name").toString();
            float formationSpeed = levelConfig.getVariable("level", "formation_speed").asFloat()
                    .orElse(Config.SPEED_DEFAULT_FORMATION_SPEED);
            float attackCooldown = levelConfig.getVariable("level", "attack_cooldown").asFloat()
                    .orElse(Config.DELAY_DEFAULT_ATTACK_COOLDOWN);
            float missileCooldown = levelConfig.getVariable("level", "missile_cooldown").asFloat()
                    .orElse(Config.DELAY_DEFAULT_MISSILE_COOLDOWN);

            Level level = new Level(name, formationSpeed, attackCooldown, missileCooldown);
            if (!levelConfig.hasSection("formation")) {
                return level;
            }

            int layers = levelConfig.getVariable("formation", "layers").asInt()
                    .orElse(Config.SIZE_DEFAULT_FORMATION_LAYERS);
            // int stages = levelConfig.getVariable("formation", "stages").asInt();

            float y = Config.POSITION_LEVEL_START_Y;
            Pair<Integer, Integer> lrIndex = Pair.of(0, 0);
            for (int i = layers; i >= 0; i--) {
                String section = "layer" + i;
                if (!levelConfig.hasSection(section)) {
                    continue;
                }

                EnemyType type = EnemyType.valueOf(levelConfig.getVariable(section, "type").toString().toUpperCase());
                Sprite sprite = Galaga.getContext().getResource().get(type);
                if (sprite == null) {
                    continue;
                }

                int count = levelConfig.getVariable(section, "count").asInt()
                        .orElse(Config.SIZE_DEFAULT_FORMATION_ENEMIES_PER_LAYER);
                float speed = levelConfig.getVariable(section, "speed").asFloat()
                        .orElse(Config.SPEED_DEFAULT_ENEMY_SPEED);
                int score = levelConfig.getVariable(section, "score").asInt().orElse(Config.SIZE_DEFAULT_ENEMY_SCORE);

                Pair<List<EnemyConfig>, Pair<Integer, Integer>> result = EnemyConfig.create(type, score, speed, count, y, level, lrIndex);
                lrIndex = result.getSecond();
                
                level.enemiesConfig.addAll(result.getFirst());

                y += sprite.getSize().getHeight() * Config.SPRITE_SCALE_DEFAULT + Config.POSITION_LEVEL_STEP_Y;
            }

            return level;
        } catch (Exception e) {
            Log.error("Level header parsing failed: %s", e.getMessage());
            return null;
        }
    }

    private static Level createLevelFromHeader(String lineHeader) {
        String[] header = lineHeader.split(" ");
        if (header.length < 4) {
            Log.error("Level header is invalid or level already exists: %s", lineHeader);
            return null;
        }

        try {
            float formationSpeed = Float.parseFloat(header[1]);
            float attackCooldown = Integer.parseInt(header[2]) * Config.DELAY_ENEMY_COOLDOWN_FACTOR_ATTACK;
            float missileCooldown = Integer.parseInt(header[3]) * Config.DELAY_ENEMY_COOLDOWN_FACTOR_MISSILE;
            return new Level(header[0], formationSpeed, attackCooldown, missileCooldown);
        } catch (NumberFormatException e) {
            Log.error("Level header parsing failed: %s", e.getMessage());
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

    public List<EnemyConfig> getEnemiesConfig() {
        return this.enemiesConfig;
    }

    public List<Enemy> getEnemies() {
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
