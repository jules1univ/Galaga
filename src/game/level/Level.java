package game.level;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import engine.utils.Position;
import engine.utils.logger.Log;
import game.Config;
import game.Galaga;
import game.entities.enemies.Enemy;
import game.entities.enemies.EnemyBee;
import game.entities.enemies.EnemyButterFly;
import game.entities.enemies.EnemyMoth;
import game.entities.enemies.EnemyType;

public class Level {
    private String name;
    private float formationSpeed;
    private int attackCooldown;
    private int missileCooldown;

    private List<Enemy> enemies = new LinkedList<>();

    public static Level createLevel(InputStream in) {

        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {
            Log.error("Level loading failed: " + e.getMessage());
            return null;
        }

        for (int i = 0; i < lines.size();) {
            Level level = createLevelFromHeader(lines.get(i));
            if (level == null) {
                return null;
            }

            i++;
            while (i < lines.size() && !lines.get(i).trim().isEmpty()) {
                Enemy enemy = createEnemyFromLine(lines.get(i));
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
            return new Level(header[0],
                    Float.parseFloat(header[1]),
                    Integer.parseInt(header[2]),
                    Integer.parseInt(header[3]));
        } catch (Exception e) {
            Log.error("Level header parsing failed: " + e.getMessage());
            return null;
        }
    }

    private static Enemy createEnemyFromLine(String line) {
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
            
            boolean leftAnimation = lockXPercent < 0.5f;
            // we no longer use size for enemies => sprite have their own fixed size
            // float size = Float.parseFloat(data[3]);


            int value = Integer.parseInt(data[4]);

            // TODO: turn this into a constant
            float speed = Float.parseFloat(data[5]) * Config.SPEED_ENEMY_FACTOR;

            EnemyType type = EnemyType.valueOf(enemyType.toUpperCase());

            switch (type) {
                case EnemyType.BEE:
                    return new EnemyBee(leftAnimation, lock, value, speed);
                case EnemyType.BUTTERFLY:
                    return new EnemyButterFly(leftAnimation,lock, value, speed);
                case EnemyType.MOTH:
                    return new EnemyMoth(leftAnimation,lock, value, speed);
                default:
                    Log.error("Unknown enemy type: " + enemyType);
                    return null;
            }
        } catch (Exception e) {
            Log.error("Level enemy parsing failed: " + e.getMessage());
            return null;
        }
    }

    private Level(String name, float formationSpeed, int attackCooldown,
            int missileCooldown) {
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

    public int getAttackCooldown() {
        return attackCooldown;
    }

    public int getMissileCooldown() {
        return missileCooldown;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }
}
