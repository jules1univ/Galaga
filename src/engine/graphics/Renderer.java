package engine.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.awt.FontMetrics;

import engine.AppFrame;
import engine.graphics.sprite.Sprite;
import engine.graphics.sprite.SpriteEntity;
import engine.graphics.sprite.SpriteManager;

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
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

        this.setFont("Default", 12);
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
        fg.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
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
            font = new Font(fontName, Font.PLAIN, size);
            this.fonts.put(fid, font);
        }

        this.g.setFont(font);
        this.fontMetrics = g.getFontMetrics();

        return this;
    }

    public Renderer drawText(String text, int x, int y, Color color) {
        g.setColor(color);
        // TODO: fix this hacky vertical alignment
        g.drawString(text, x + fontMetrics.getHeight() / 2, y + fontMetrics.getHeight() * 2);
        return this;
    }

    public Renderer drawRect(int x, int y, int width, int height, Color color) {
        this.g.setColor(color);
        this.g.fillRect(x, y, width, height);
        return this;
    }

    public Renderer drawSprite(String name, int x, int y) {
        BufferedImage img = SpriteManager.getInstance().get(name).getImage();
        if (img == null) {
            return this;
        }
        this.g.drawImage(img, x, y, (int) (img.getWidth()), (int) (img.getHeight()), null);
        return this;
    }

    public Renderer drawSprite(Sprite sprite, int x, int y) {
        if (sprite == null || sprite.getImage() == null) {
            return this;
        }
        this.g.drawImage(sprite.getImage(), x, y, null);
        return this;
    }

    public Renderer drawSpriteEntity(SpriteEntity e) {
        Sprite sprite = e.getSprite();
        if (sprite == null || sprite.getImage() == null) {
            return this;
        }
        this.g.drawImage(sprite.getImage(),
                (int) (e.getOffsetX()),
                (int) (e.getOffsetY()),
                (int) (e.getWidth()),
                (int) (e.getHeight()),
                null);
        return this;
    }
}
