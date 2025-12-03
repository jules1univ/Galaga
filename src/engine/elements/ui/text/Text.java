package engine.elements.ui.text;

import engine.Application;
import engine.elements.ui.UIElement;
import engine.utils.Position;
import java.awt.Color;
import java.awt.Font;

public final class Text extends UIElement {

    private Font font;
    private Color color;
    private String text;
    private Position initial;

    private TextPosition horizontal;
    private TextPosition vertical;

    public Text(String text, Position position, Color color, Font font) {
        super();
        this.text = text;

        this.position = position.copy();
        this.initial = position.copy();

        this.font = font;
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

    public void setFont(Font font) {
        this.font = font;
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
        this.size = Application.getContext().getRenderer().getTextSize(this.text, this.font);

        switch (this.horizontal) {
            case BEGIN ->
                this.position.setX(this.initial.getX() + this.size.getWidth());
            case CENTER ->
                this.position.setX(this.initial.getX() - this.size.getWidth() / 2);
            case END ->
                this.position.setX(this.initial.getX() - this.size.getWidth());
            default ->
                this.position.setX(this.initial.getX());
        }

        switch (this.vertical) {
            case BEGIN ->
                this.position.setY(this.initial.getY() + this.size.getHeight());
            case CENTER ->
                this.position.setY(this.initial.getY() + this.size.getHeight()/2);
            case END ->
                this.position.setY(this.initial.getY() - this.size.getHeight());
            default ->
                this.position.setY(this.initial.getY());
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
        Application.getContext().getRenderer().drawText(this.text, this.position, this.color, this.font);
    }

}
