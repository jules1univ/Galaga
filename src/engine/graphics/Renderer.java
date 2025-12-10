package engine.graphics;

import engine.AppFrame;
import engine.Application;
import engine.elements.entity.SpriteEntity;
import engine.graphics.sprite.Sprite;
import engine.utils.Position;
import engine.utils.Size;
import engine.utils.logger.Log;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public final class Renderer {

    private final AppFrame frame;

    private final BufferedImage backBuffer;
    private final Graphics2D g;

    public Renderer(AppFrame frame) {
        this.frame = frame;

        this.backBuffer = new BufferedImage(this.frame.getWidth(), this.frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
        this.g = backBuffer.createGraphics();

        this.g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        this.g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        this.g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
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

    public Size getTextSize(String text, Font font) {
        if (font == null || text == null || text.isEmpty()) {
            Log.warning("Attempted to get size of null or empty text.");
            return Size.zero();
        }
        this.g.setFont(font);
        Rectangle2D rect = font.getStringBounds(text, this.g.getFontRenderContext());
        return Size.of(
                (float) rect.getWidth(),
                (float) rect.getHeight());
    }

    public Renderer drawText(String text, Position position, Color color, Font font) {
        if (font == null || text == null || text.isEmpty()) {
            Log.warning("Attempted to draw null or empty text.");
            return this;
        }
        this.g.setColor(color);
        this.g.setFont(font);

        this.g.drawString(text, position.getIntX(), position.getIntY());

        if (Application.DEBUG_MODE) {
            Size size = this.getTextSize(text, font);
            this.g.drawRect(position.getIntX(), position.getIntY() - size.getIntHeight(), size.getIntWidth(), size.getIntHeight());
        }
        return this;
    }

    public Renderer drawRect(Position position, Size size, Color color) {
        this.g.setColor(color);
        this.g.fillRect(position.getIntX(), position.getIntY(), size.getIntWidth(), size.getIntHeight());
        return this;
    }

    public Renderer drawRect(Position position, Size size, Color color, float angle) {
        this.g.setColor(color);

        float cx = position.getX() + size.getWidth() / 2;
        float cy = position.getY() + size.getHeight() / 2;

        AffineTransform old = this.g.getTransform();
        this.g.rotate(Math.toRadians(angle), cx, cy);
        this.drawRect(position, size, color);
        this.g.setTransform(old);

        return this;
    }

    public Renderer drawRectOutline(Position position, Size size, Color color) {
        this.g.setColor(color);
        this.g.drawRect(position.getIntX(), position.getIntY(), size.getIntWidth(), size.getIntHeight());
        return this;
    }

    public Renderer drawLoadingCircle(Position center, float radius, Color color, int segments, float percent) {
        this.g.setColor(color);

        float angleStep = 360.f / segments;
        float filledAngle = 360.f * percent;

        for (float angle = 0.f; angle < filledAngle; angle += angleStep) {
            float nextAngle = Math.min(angle + angleStep, filledAngle);

            int x1 = (int) (center.getX() + Math.cos(Math.toRadians(angle)) * radius);
            int y1 = (int) (center.getY() + Math.sin(Math.toRadians(angle)) * radius);
            int x2 = (int) (center.getX() + Math.cos(Math.toRadians(nextAngle)) * radius);
            int y2 = (int) (center.getY() + Math.sin(Math.toRadians(nextAngle)) * radius);

            int[] xPoints = {center.getIntX(), x1, x2};
            int[] yPoints = {center.getIntY(), y1, y2};

            this.g.fillPolygon(xPoints, yPoints, 3);
        }

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
        if (e.getAngle() == 0f) {

            this.g.drawImage(sprite.getImage(), center.getIntX(), center.getIntY(), size.getIntWidth(),
                    size.getIntHeight(),
                    null);
            return this;
        }

        float cx = center.getX() + e.getScaledSize().getWidth() / 2;
        float cy = center.getY() + e.getScaledSize().getHeight() / 2;

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
        this.g.drawLine(0, centerY, this.frame.getWidth(), centerY);

        return this;
    }
}
