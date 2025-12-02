package engine.utils.cache;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import engine.utils.logger.Log;

public final class Cache {

    public static boolean exists(String name) {
        String alias = Integer.toHexString(name.hashCode());
        File cache = new File("./cache", alias + ".cache");
        return cache.exists();
    }

    public static InputStream load(String name) {
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

    public static void save(String name, InputStream in) {
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

    public static void save(String name, byte[] data) {
        save(name, new ByteArrayInputStream(data));
    }


}
