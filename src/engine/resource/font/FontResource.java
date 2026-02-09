package engine.resource.font;

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
    public Font read(InputStream in) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, in);
            ResourceVariant variant = this.alias.getVariant();

            if (variant != null) {
                float size = variant.getValue();
                font = font.deriveFont(size);
            }
            return font;
        } catch (FontFormatException | IOException e) {
            Log.error("Font loading failed: %s", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean write(Font data) {
        throw new UnsupportedOperationException("Font.write should not be called");
    }
}
