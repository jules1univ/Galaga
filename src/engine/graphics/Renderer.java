package engine.graphics;

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
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Rectangle2D;

public final class Renderer {

    private Graphics2D g;

    public Renderer() {
    }

    public void set(Graphics2D g) {
        this.g = g;
        this.g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        this.g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    public void begin() {
        this.g.setColor(Color.BLACK);
        this.g.fillRect(0, 0, Application.getContext().getFrame().getWidth(),
                Application.getContext().getFrame().getHeight());
    }

    public void end() {
        this.g.dispose();
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
            this.g.drawRect(position.getIntX(), position.getIntY() - size.getIntHeight(), size.getIntWidth(),
                    size.getIntHeight());
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

            int[] xPoints = { center.getIntX(), x1, x2 };
            int[] yPoints = { center.getIntY(), y1, y2 };

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

        this.g.drawImage(sprite.getImage(), center.getIntX(), center.getIntY(), size.getIntWidth(),
                size.getIntHeight(),
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

    public Renderer drawGrid(int width, int height, int cellSize, Color color) {
        this.g.setColor(color);

        for (int x = 0; x < width; x += cellSize) {
            this.g.drawLine(x, 0, x, height);
        }

        for (int y = 0; y < height; y += cellSize) {
            this.g.drawLine(0, y, width, y);
        }

        return this;
    }

    public Renderer drawCubicBezier(Position start, Position control1, Position control2, Position end, Color color) {
        this.g.setColor(color);

        CubicCurve2D curve = new CubicCurve2D.Float(
                start.getX(), start.getY(),
                control1.getX(), control1.getY(),
                control2.getX(), control2.getY(),
                end.getX(), end.getY());
        this.g.draw(curve);

        return this;
    }

}
