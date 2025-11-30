package engine.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
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
    private FontMetrics fontMetrics;

    public Renderer(AppFrame frame) {
        this.frame = frame;

        this.backBuffer = new BufferedImage(this.frame.getWidth(), this.frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
        this.g = backBuffer.createGraphics();

        this.g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        this.g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        this.g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);

        this.fontMetrics = this.g.getFontMetrics(this.g.getFont());

    }

    public void begin() {
        this.g.setColor(Color.BLACK);
        this.g.fillRect(0, 0, frame.getWidth(), frame.getHeight());
    }

    public void end() {
        Graphics2D fg = (Graphics2D)this.frame.getGraphics();
        if (fg == null) {
            return;
        }
        fg.drawImage(this.backBuffer, 0, 0, null);
        fg.dispose();
    }

    public void clear(Color color) {
        this.g.setColor(color);
        this.g.fillRect(0, 0, frame.getWidth(), frame.getHeight());
    }

    public Renderer setFont(Font font) {
        if (font == null) {
            Log.warning("Attempted to set null font.");
            return this;
        }
        this.fontMetrics = this.g.getFontMetrics(font);
        this.g.setFont(font);
        return this;
    }

    public Renderer setFont(String alias) {
        return this.setFont(FontManager.getInstance().getFont(alias));
    }

    public int getTextWidth(String text) {
        return this.fontMetrics.stringWidth(text) + this.fontMetrics.getAscent() * 2;
    }

    public Renderer drawText(String text, int x, int y, Color color) {
        this.g.setColor(color);

        // FIXME: hacky way to align text properly
        this.g.drawString(text, x + this.fontMetrics.getAscent(),
                y + this.fontMetrics.getAscent() + this.fontMetrics.getLeading() + this.fontMetrics.getHeight());
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
        int y = (int) (offsetY - (img.getHeight() * scale) / 2);

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
