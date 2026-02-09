package galaga.resources.level;

import engine.graphics.sprite.Sprite;
import engine.resource.Resource;
import engine.resource.ResourceAlias;
import engine.resource.ResourceCallback;
import engine.utils.logger.Log;
import galaga.Config;
import galaga.Galaga;
import galaga.entities.enemies.EnemyConfig;
import galaga.entities.enemies.EnemyType;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LevelResource extends Resource<Level> {
    public static final String NAME = "level";

    public LevelResource(ResourceAlias alias, ResourceCallback callback) {
        super(alias, callback);
    }

    @Override
    public Level read(InputStream in) {
        return Level.create(in);
    }

    @Override
    public boolean write(Level data) {
        OutputStream out = this.getResourceOutput();
        if (out == null) {
            return false;
        }

        try {
            out.write(data.getName().getBytes());
            out.write(' ');
            out.write(Float.toString(data.getFormationSpeed()).getBytes());
            out.write(' ');
            out.write(Float.toString(data.getAttackCooldown()).getBytes());
            out.write(' ');
            out.write(Float.toString(data.getMissileCooldown()).getBytes());
            out.write('\n');

            Sprite enemySprite = Galaga.getContext().getResource().get(EnemyType.BEE);
            if (enemySprite == null) {
                out.flush();
                return true;
            }
            float width = enemySprite.getSize().getWidth() / Config.WINDOW_WIDTH;

            for (EnemyConfig enemy : data.getEnemiesConfig()) {
                out.write(enemy.getType().name().getBytes());
                out.write(' ');

                float percentX = enemy.getLockPosition().getX() / Config.WINDOW_WIDTH;
                out.write(Float.toString(percentX).getBytes());
                out.write(' ');

                float percentY = enemy.getLockPosition().getY() / Config.WINDOW_HEIGHT;
                out.write(Float.toString(percentY).getBytes());
                out.write(' ');

                out.write(Float.toString(width).getBytes());
                out.write(' ');

                out.write(Integer.toString(enemy.getScoreValue()).getBytes());
                out.write(' ');

                out.write(Float.toString(enemy.getSpeed()).getBytes());
                out.write('\n');
            }

            out.flush();
            return true;
        } catch (IOException e) {
            Log.error("Level saving failed: %s", e.getMessage());
            return false;
        }
    }
}
