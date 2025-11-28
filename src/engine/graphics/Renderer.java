package engine.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.awt.FontMetrics;
import java.awt.geom.AffineTransform;

import engine.AppFrame;
import engine.entity.SpriteEntity;
import engine.graphics.sprite.Sprite;
import engine.utils.logger.Log;

public final class Renderer {

    private final AppFrame frame;

    private BufferedImage backBuffer;
    private Graphics2D g;
    private HashMap<String, Font> fonts = new HashMap<>();
    private FontMetrics fontMetrics;

    public Renderer(AppFrame frame) {
        this.frame = frame;

        this.backBuffer = new BufferedImage(this.frame.getWidth(), this.frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
        this.g = backBuffer.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
    }

    public void begin() {
        this.g.setColor(Color.BLACK);
        this.g.fillRect(0, 0, frame.getWidth(), frame.getHeight());
    }

    public void end() {
        Graphics2D fg = (Graphics2D) frame.getGraphics();
        if (fg == null) {
            return;
        }
        fg.drawImage(backBuffer, 0, 0, null);
        fg.dispose();
    }

    public void clear(Color color) {
        this.g.setColor(color);
        this.g.fillRect(0, 0, frame.getWidth(), frame.getHeight());
    }

    public Renderer setFont(String fontName, int size) {
        Font font;
        String fid = fontName + "@" + size;
        if (this.fonts.containsKey(fid)) {
            font = this.fonts.get(fid);
        } else {
            String[] names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            if (!Arrays.asList(names).contains(fontName)) {
                Log.warning("Font '" + fontName + "' not found. Using default font.");
                fontName = "Default";
            }

            font = new Font(fontName, Font.PLAIN, size);
            this.fonts.put(fid, font);
        }

        this.g.setFont(font);
        this.fontMetrics = g.getFontMetrics();

        return this;
    }

    public int getTextWidth(String text) {
        return this.fontMetrics.stringWidth(text);
    }

    public Renderer drawText(String text, int x, int y, Color color) {
        this.g.setColor(color);
        this.g.drawString(text, x, y + this.fontMetrics.getHeight() * 2);
        return this;
    }

    public Renderer drawRect(float x, float y, float width, float height, Color color) {
        this.g.setColor(color);
        this.g.fillRect((int) x, (int) y, (int) width, (int) height);
        return this;
    }

    public Renderer drawSprite(Sprite sprite, float offsetX, float offsetY, float scale) {
        if (sprite == null || sprite.getImage() == null) {
            Log.warning("Attempted to draw null sprite.");
            return this;
        }

        BufferedImage img = sprite.getImage();

        int x = (int) (offsetX - (img.getWidth() * scale) / 2);
        int y = (int)  (offsetY - (img.getHeight() * scale) / 2);

        this.g.drawImage(img, x, y, (int) (img.getWidth() * scale), (int) (img.getHeight() * scale), null);
        return this;
    }

    public Renderer drawSpriteEntity(SpriteEntity e) {
        Sprite sprite = e.getSprite();
        if (sprite == null || sprite.getImage() == null) {
            Log.warning("Attempted to draw null sprite entity.");
            return this;
        }
        BufferedImage img = sprite.getImage();

        int x = (int) (e.getOffsetX() - e.getWidth() / 2);
        int y = (int) (e.getOffsetY() - e.getHeight() / 2);

        if (e.getAngle() == 0.0f) {
            this.g.drawImage(img, x, y, (int) e.getWidth(), (int) e.getHeight(), null);
            return this;
        }

        double cx = x + e.getWidth() / 2;
        double cy = y + e.getHeight() / 2;

        AffineTransform old = this.g.getTransform();
        this.g.rotate(Math.toRadians(e.getAngle()), cx, cy);
        this.g.drawImage(img, x, y, (int) e.getWidth(), (int) e.getHeight(), null);
        this.g.setTransform(old);

        return this;
    }
}
