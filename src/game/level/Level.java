package game.level;

import java.util.LinkedList;
import java.util.List;

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

    public Level(String name, float formationSpeed, int attackCooldown,
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

    public boolean addEnemy(EnemyType type, float endX, float endY, float size, int value, float speed) {
        switch (type) {
            case EnemyType.BEE:
                enemies.add(new EnemyBee(endX, endY, size, value, speed));
                break;
            case EnemyType.BUTTERFLY:
                enemies.add(new EnemyButterFly(endX, endY, size, value, speed));
                break;
            case EnemyType.MOTH:
                enemies.add(new EnemyMoth(endX, endY, size, value, speed));
                break;
            default:
                return false;
        }
        return true;
    }
}
