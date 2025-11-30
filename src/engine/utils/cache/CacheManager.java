package engine.utils.cache;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import engine.utils.logger.Log;

public class CacheManager {
    private static CacheManager inst = null;

    public static CacheManager getInstance() {
        if (inst == null) {
            inst = new CacheManager();
        }
        return inst;
    }

    public boolean exists(String name) {
        String alias = Integer.toHexString(name.hashCode());
        File cache = new File("./cache", alias + ".cache");
        return cache.exists();
    }

    public InputStream load(String name) {
        try {
            String alias = Integer.toHexString(name.hashCode());
            File cache = new File("./cache", alias + ".cache");
            if (cache.exists()) {
                Log.message("Cache loaded: " + cache.toString());
                return new FileInputStream(cache);
            }
        } catch (Exception e) {
            Log.error("Cache loading failed: " + e.getMessage());
        }
        return null;
    }

    public void save(String name, InputStream in) {
        try {
            String alias = Integer.toHexString(name.hashCode());
            File dir = new File("./cache");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File cache = new File(dir, alias + ".cache");
            if (cache.exists()) {
                return;
            }

            new Thread(() -> {
                try (FileOutputStream out = new FileOutputStream(cache)) {
                    in.transferTo(out);
                    Log.message("Cache saved: " + cache.toString());
                } catch (Exception e) {
                    Log.error("Cache saving failed: " + e.getMessage());
                }
            }).start();

        } catch (Exception e) {
            Log.error("Cache saving failed: " + e.getMessage());
        }
    }

    public void save(String name, byte[] data) {
        this.save(name, new ByteArrayInputStream(data));
    }

    public void save(String name, BufferedImage image) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", out);
            this.save(name, out.toByteArray());
        } catch (Exception e) {}
    }

}
