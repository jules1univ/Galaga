package engine.ui.text;

import java.awt.Color;
import engine.ui.UIElement;
import game.Galaga;

public final class Text extends UIElement {

    private final Color color;
    private final int size;

    private final float initialY;
    private final float initialX;

    private String text;

    private TextPosition horizontal;
    private TextPosition vertical;

    public Text(String text, float x, float y, int size, Color color) {
        super();
        this.text = text;

        this.initialY = y;
        this.initialX = x;

        this.size = size;
        this.color = color;
        this.horizontal = TextPosition.BEGIN;
        this.vertical = TextPosition.BEGIN;
    }

    public void setText(String text) {
        this.text = text;
        this.updateTextPosition();
    }

    public String getText() {
        return this.text;
    }

    public void setCenter(TextPosition horizontal, TextPosition vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
        this.updateTextPosition();
    }

    private void updateTextPosition() {
        int textWidth = Galaga.getContext().getRenderer().getTextWidth(this.text);

        if (this.horizontal == TextPosition.CENTER) {
            this.x = this.initialX - textWidth / 2;
        } else if (this.horizontal == TextPosition.END) {
            this.x = this.initialX - textWidth;
        } else {
            this.x = this.initialX;
        }

        if (this.vertical == TextPosition.CENTER) {
            this.y = this.initialY - this.size / 2;
        } else if (this.vertical == TextPosition.BEGIN) {
            this.y = this.initialY - this.size;
        } else {
            this.y = this.initialY;
        }

    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public void update(double dt) {
    }

    @Override
    public void draw() {
        Galaga.getContext().getRenderer().drawText(this.text, (int) this.x, (int) this.y, this.color);
    }

}
