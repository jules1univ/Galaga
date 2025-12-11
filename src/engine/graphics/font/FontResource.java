package engine.graphics.font;

import engine.resource.Resource;
import engine.resource.ResourceAlias;
import engine.resource.ResourceCallback;
import engine.resource.ResourceVariant;
import engine.utils.logger.Log;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

public final class FontResource extends Resource<Font> {
    public static final String NAME = "font";

    public FontResource(ResourceAlias alias, ResourceCallback callback) {
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
                float size = variant.getValue();
                font = font.deriveFont(size);
            }
            this.onLoadComplete(font);
        } catch (FontFormatException | IOException e) {
            Log.error("Font loading failed: " + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean write(Font data) {
        throw new UnsupportedOperationException("Font.write should not be called");
    }

}
