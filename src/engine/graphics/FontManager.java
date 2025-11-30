package engine.graphics;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;

import engine.utils.logger.Log;

public class FontManager {
    private HashMap<String, Font> fonts = new HashMap<>();
    private static FontManager inst = null;

    public static FontManager getInstance() {
        if (inst == null) {
            inst = new FontManager();
        }
        return inst;
    }

    public boolean load(String fontName, int size, String alias) {
        if (fonts.containsKey(alias)) {
            Log.warning("Font " + alias + " is already loaded.");
            return true;
        }

        String[] names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        if (!Arrays.asList(names).contains(fontName)) {
            Log.warning("Font '" + fontName + "' not found.");
            return false;
        }

        Font font = new Font(fontName, Font.PLAIN, size);
        fonts.put(alias, font);
        Log.message("Font '" + alias + "' loaded successfully");
        return true;
    }

    public boolean loadFromUrl(String url, int size, String alias) {
        try {
            String urlAlias = Integer.toHexString(url.hashCode());
            Font font;

            InputStream is = this.loadFontFromCache(urlAlias);
            if (is != null) {
                font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.PLAIN, size);
                Log.message("Font '" + alias + "' loaded from cache.");
            } else {
                is = URI.create(url).toURL().openStream();
                byte[] data = is.readAllBytes();
                is.close();

                if (!this.saveFontToCache(urlAlias, data)) {
                    Log.warning("Font '" + alias + "' could not be cached.");
                }
                font = Font.createFont(Font.TRUETYPE_FONT, new ByteArrayInputStream(data)).deriveFont(Font.PLAIN, size);
            }

            fonts.put(alias, font);
            Log.message("Font '" + alias + "' loaded successfully.");
            return true;
        } catch (Exception e) {
            Log.error("Font loading from url failed: " + e.getMessage());
            return false;
        }
    }

    public Font getFont(String alias) {
        return fonts.get(alias);
    }

    private InputStream loadFontFromCache(String alias) {
        try {
            File cacheFont = new File("./cache", alias + ".cache");
            if (cacheFont.exists()) {
                return new FileInputStream(cacheFont);
            }
        } catch (Exception e) {
        }
        return null;

    }

    private boolean saveFontToCache(String alias, byte[] data) {
        try {
            File dir = new File("./cache");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File cacheFont = new File(dir, alias + ".cache");
            if (cacheFont.exists()) {
                return true;
            }
            try (FileOutputStream out = new FileOutputStream(cacheFont)) {
                out.write(data);
            }

            return true;
        } catch (Exception e) {
        }
        return false;
    }
}
