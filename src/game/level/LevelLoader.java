package game.level;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import game.Galaga;
import game.entities.enemies.EnemyType;

public class LevelLoader {
    private HashMap<String, Level> levels = new HashMap<>();

    public LevelLoader() {
    }

    private Level parseHeader(String lineHeader) {
        String[] header = lineHeader.split(" ");
        if (header.length < 4 || this.levels.containsKey(header[0])) {
            return null;
        }

        try {
            return new Level(header[0],
                    Float.parseFloat(header[1]),
                    Integer.parseInt(header[2]),
                    Integer.parseInt(header[3]));
        } catch (Exception e) {
            return null;
        }
    }

    private boolean parseEnemy(String lineEnemy, Level level) {
        String[] data = lineEnemy.split(" ");
        if (data.length < 6) {
            return false;
        }
        String enemyType = data[0];
        try {
            float startXPercent = Float.parseFloat(data[1]);
            float startX = (1.f - startXPercent) * Galaga.getContext().getFrame().getWidth();

            float startYPercent = Float.parseFloat(data[2]);
            float startY = (1.f - startYPercent) * Galaga.getContext().getFrame().getHeight();

            float size = Float.parseFloat(data[3]);
            int value = Integer.parseInt(data[4]);
            float speed = Float.parseFloat(data[5]);

            EnemyType type = EnemyType.valueOf(enemyType.toUpperCase());

            return level.addEnemy(type, startX, startY, size, value, speed);
        } catch (Exception e) {
            return false;
        }
    }

    public String load(String path) {
        ArrayList<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {
            return null;
        }

        for (int i = 0; i < lines.size();) {
            Level level = this.parseHeader(lines.get(i));
            if (level == null) {
                return null;
            }

            i++;
            while (i < lines.size() && !lines.get(i).trim().isEmpty()) {
                if (!this.parseEnemy(lines.get(i), level)) {
                    return null;
                }
                i++;
            }

            levels.put(level.getName(), level);
            return level.getName();
        }

        return null;
    }

    public List<String> getLevelNames() {
        return this.levels.keySet().stream().toList();
    }
}
