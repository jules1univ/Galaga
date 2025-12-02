package engine.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.FontMetrics;
import java.awt.geom.AffineTransform;

import engine.AppFrame;
import engine.elements.entity.SpriteEntity;
import engine.graphics.sprite.Sprite;
import engine.utils.Position;
import engine.utils.Size;
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
        Graphics2D fg = (Graphics2D) this.frame.getGraphics();
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

    public Renderer setFont(Font font, int size) {
        Font derived = font.deriveFont((float) size);
        this.fontMetrics = this.g.getFontMetrics(derived);
        this.g.setFont(derived);
        return this;
    }

    public Renderer setFont(Font font) {
        this.fontMetrics = this.g.getFontMetrics(font);
        this.g.setFont(font);
        return this;
    }

    public boolean isFont(Font font) {
        return this.g.getFont().equals(font);
    }

    public int getTextWidth(String text) {
        return this.fontMetrics.stringWidth(text) + this.fontMetrics.getAscent() * 2;
    }

    public Renderer drawText(String text, Position position, Color color) {
        this.g.setColor(color);

        // FIXME: hacky way to align text properly
        this.g.drawString(text, position.getIntX() + this.fontMetrics.getAscent(),
                position.getIntY() + this.fontMetrics.getAscent() + this.fontMetrics.getLeading()
                        + this.fontMetrics.getHeight());
        return this;
    }

    public Renderer drawRect(Position position, Size size, Color color) {
        this.g.setColor(color);
        this.g.fillRect(position.getIntX(), position.getIntY(), size.getIntWidth(), size.getIntHeight());
        return this;
    }

    public Renderer drawSprite(Sprite sprite, Position position, float scale) {
        if (sprite == null || sprite.getImage() == null) {
            Log.warning("Attempted to draw null sprite.");
            return this;
        }

        Size size = Size.of(sprite.getSize(), scale);
        Position center = Position.ofCenter(position, size);

        this.g.drawImage(sprite.getImage(), center.getIntX(), center.getIntY(), size.getIntWidth(), size.getIntHeight(),
                null);
        return this;
    }

    public Renderer drawSprite(SpriteEntity e) {
        Sprite sprite = e.getSprite();
        if (sprite == null || sprite.getImage() == null) {
            Log.warning("Attempted to draw null sprite entity.");
            return this;
        }

        Position center = e.getCenter();
        Size size = e.getScaledSize();
        if (e.getAngle() == 0.0f) {

            this.g.drawImage(sprite.getImage(), center.getIntX(), center.getIntY(), size.getIntWidth(),
                    size.getIntHeight(),
                    null);
            return this;
        }

        double cx = center.getX() + e.getScaledSize().getWidth() / 2;
        double cy = center.getY() + e.getScaledSize().getHeight() / 2;

        AffineTransform old = this.g.getTransform();
        this.g.rotate(Math.toRadians(e.getAngle()), cx, cy);

        this.g.drawImage(sprite.getImage(), center.getIntX(), center.getIntY(), size.getIntWidth(),
                size.getIntHeight(),
                null);

        this.g.setTransform(old);

        return this;
    }

    public Renderer drawGrid(int cellSize, Color color) {
        this.g.setColor(color);

        for (int x = 0; x < this.frame.getWidth(); x += cellSize) {
            this.g.drawLine(x, 0, x, this.frame.getHeight());
        }

        for (int y = 0; y < this.frame.getHeight(); y += cellSize) {
            this.g.drawLine(0, y, this.frame.getWidth(), y);
        }

        return this;
    }

    public Renderer drawCross(Color color) {
        this.g.setColor(color);

        int centerX = this.frame.getWidth() / 2;
        int centerY = this.frame.getHeight() / 2;
        
        this.g.drawLine(centerX, 0, centerX, this.frame.getHeight());
        this.g.drawLine(0,  centerY, this.frame.getWidth(), centerY);

        return this;
    }
}
