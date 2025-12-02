package engine.elements.ui.text;

import java.awt.Color;

import engine.elements.ui.UIElement;
import engine.utils.Position;
import game.Galaga;

public final class Text extends UIElement {

    private final Color color;
    private final int size;

    private final Position initial;

    private String text;

    private TextPosition horizontal;
    private TextPosition vertical;

    public Text(String text, Position initial, int size, Color color) {
        super();
        this.text = text;

        this.initial = initial.copy();
        this.position = initial.copy();

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
            this.position.setX(this.initial.getX() - textWidth / 2);
        } else if (this.horizontal == TextPosition.END) {
            this.position.setX(this.initial.getX() - textWidth);
        } else {
            this.position.setX(this.initial.getX());
        }

        if (this.vertical == TextPosition.BEGIN) {
            this.position.setY(this.initial.getY() - this.size * 1.5f);
        } else if (this.vertical == TextPosition.CENTER) {
            this.position.setY(this.initial.getY() - this.size);
        } else {
            this.position.setY(this.initial.getY() - this.size / 2);
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
        Galaga.getContext().getRenderer().drawText(this.text, this.position, this.color);
    }

}
