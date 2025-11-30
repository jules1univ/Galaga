package engine.graphics.font;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;

import engine.utils.cache.CacheManager;
import engine.utils.logger.Log;

public class FontManager {
    private HashMap<String, Font> fonts = new HashMap<>();
    private static FontManager inst = null;

    // int because multiple fonts can be downloaded at the same time
    private volatile int downloadUpdates = 0;

    public static FontManager getInstance() {
        if (inst == null) {
            inst = new FontManager();
        }
        return inst;
    }

    private String[] availableFonts;

    private FontManager() {
        this.availableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        if (this.availableFonts.length == 0) {
            Log.warning("No available fonts found on the system.");
            this.availableFonts = new String[] { "Default" };
        }
    }

    public boolean load(String fontName, int size, String alias) {
        if (fonts.containsKey(alias)) {
            Log.warning("Font " + alias + " is already loaded.");
            return true;
        }

        if (!Arrays.asList(this.availableFonts).contains(fontName)) {
            Log.warning("Font '" + fontName + "' not found.");
            return false;
        }

        Font font = new Font(fontName, Font.PLAIN, size);
        fonts.put(alias, font);
        Log.message("Font '" + alias + "' loaded successfully");
        return true;
    }

    public boolean loadFromUrl(String url, int size, String alias) {
        if (fonts.containsKey(alias)) {
            Log.warning("Font " + alias + " is already loaded.");
            return true;
        }

        try {
            if (CacheManager.getInstance().exists(url)) {
                Log.message("Font '" + alias + "' found in cache.");
                Font font = Font.createFont(Font.TRUETYPE_FONT, CacheManager.getInstance().load(url));
                fonts.put(alias, font.deriveFont(Font.PLAIN, size));
                Log.message("Font '" + alias + "' loaded successfully.");
                return true;
            }

            Log.message("Font '" + alias + "' not found in cache. Downloading from: " + url);

            // create a temporary font to use while load the real font
            fonts.put(alias, new Font(this.availableFonts[0], Font.PLAIN, size));

            new Thread(() -> {
                try {
                    byte[] data = URI.create(url).toURL().openStream().readAllBytes();
                    CacheManager.getInstance().save(url, data);

                    Font font = Font.createFont(Font.TRUETYPE_FONT, new ByteArrayInputStream(data));
                    this.fonts.replace(alias, font.deriveFont(Font.PLAIN, size));
                    this.downloadUpdates++;

                    Log.message("Font '" + alias + "' loaded successfully from url.");
                } catch (Exception e) {
                    Log.error("Font loading from url failed: " + e.getMessage());

                }
            }).start();

            return true;
        } catch (Exception e) {
            Log.error("Font loading from url failed: " + e.getMessage());
            return false;
        }
    }

    public boolean hasUpdate() {
        return this.downloadUpdates > 0;
    }

    public void clearUpdate() {
        this.downloadUpdates = 0;
    }

    public Font getFont(String alias) {
        return fonts.get(alias);
    }
}
