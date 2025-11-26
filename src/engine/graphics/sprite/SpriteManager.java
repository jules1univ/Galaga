package engine.graphics.sprite;

import java.util.HashMap;
import java.util.Map;

public final class SpriteManager {

    private static SpriteManager inst = null;
    private final Map<String, Sprite> sprites = new HashMap<>();

    private SpriteManager() {
    }

    public static SpriteManager getInstance() {
        if (inst == null) {
            inst = new SpriteManager();
        }
        return inst;
    }

    public boolean load(String name, String path, float scale) {
        Sprite sprite = new Sprite();
        if (!sprite.load(path, scale)) {
            return false;
        }

        sprites.put(name, sprite);
        return true;
    }

    public boolean load(String name, String path) {
        return this.load(name, path, 1.f);
    }

    public Sprite get(String name) {
        return sprites.get(name);
    }
}
