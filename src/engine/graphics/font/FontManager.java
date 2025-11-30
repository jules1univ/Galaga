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
            Font font;
            if( CacheManager.getInstance().exists(url))
            {
                Log.message("Font '" + alias + "' found in cache.");
                font = Font.createFont(Font.TRUETYPE_FONT, CacheManager.getInstance().load(url));
            } else {    
                byte[] data = URI.create(url).toURL().openStream().readAllBytes();
                
                CacheManager.getInstance().save(url, data);
                font = Font.createFont(Font.TRUETYPE_FONT, new ByteArrayInputStream(data));
            }

            fonts.put(alias, font.deriveFont(Font.PLAIN, size));
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
}
