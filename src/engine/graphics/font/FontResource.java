package engine.graphics.font;

import java.awt.Font;
import java.io.InputStream;

import engine.resource.Resource;
import engine.resource.ResourceAlias;
import engine.resource.ResourceVariant;
import engine.utils.logger.Log;

public final class FontResource extends Resource<Font> {

    public FontResource(ResourceAlias alias, Runnable callback) {
        super(alias, callback);
    }

    @Override
    public boolean load() {
        InputStream in = this.getResourceData();
        if (in == null) {
            return false;
        }

        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, in);

            ResourceVariant variant = this.alias.getVariant();
            if (variant != null) {
                int size = variant.getValue();
                font = font.deriveFont((float) size);
            }
            this.onLoadComplete(font);
        } catch (Exception e) {
            Log.error("Font loading failed: " + e.getMessage());
            return false;
        }
        return true;
    }

}
