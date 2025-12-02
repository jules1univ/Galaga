package engine.elements.ui.text;

import java.awt.Color;

import engine.Application;
import engine.elements.ui.UIElement;
import engine.utils.Position;
import engine.utils.Size;

public final class Text extends UIElement {

    private final int height;

    private Color color;
    private String text;
    private Position initial;

    private TextPosition horizontal;
    private TextPosition vertical;

    public Text(String text, Position position, int size, Color color) {
        super();
        this.text = text;

        this.position = position.copy();
        this.initial = position.copy();

        this.height = size;
        this.color = color;
        this.horizontal = TextPosition.BEGIN;
        this.vertical = TextPosition.BEGIN;
    }

    @Override
    public void setPosition(Position position) {
        super.setPosition(position);
        this.initial = position.copy();
        this.updateText();
    }

    public void setText(String text) {
        this.text = text;
        this.updateText();
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

    public String getText() {
        return this.text;
    }

    public void setCenter(TextPosition horizontal, TextPosition vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
        this.updateText();
    }

    private void updateText() {
        this.size = Size.of(
                Application.getContext().getRenderer().getTextWidth(this.text),
                this.height);

        if (this.horizontal == TextPosition.CENTER) {
            this.position.setX(this.initial.getX() - this.size.getWidth() / 2);
        } else if (this.horizontal == TextPosition.END) {
            this.position.setX(this.initial.getX() - this.size.getWidth());
        } else {
            this.position.setX(this.initial.getX());
        }

        if (this.vertical == TextPosition.BEGIN) {
            this.position.setY(this.initial.getY() - this.height * 1.5f);
        } else if (this.vertical == TextPosition.CENTER) {
            this.position.setY(this.initial.getY() - this.height);
        } else {
            this.position.setY(this.initial.getY() - this.height / 2);
        }

    }

    @Override
    public boolean init() {
        this.updateText();
        return true;
    }

    @Override
    public void update(double dt) {
    }

    @Override
    public void draw() {
        Application.getContext().getRenderer().drawText(this.text, this.position, this.color);
    }

}
