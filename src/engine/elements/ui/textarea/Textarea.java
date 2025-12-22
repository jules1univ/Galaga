package engine.elements.ui.textarea;

import engine.Application;
import engine.elements.ui.UIElement;
import engine.elements.ui.text.TextPosition;
import engine.utils.Position;
import java.awt.Color;
import java.awt.Font;
import java.util.List;

public final class Textarea extends UIElement {

    private Font font;
    private Color color;
    private List<String> lines;

    private Position initial;
    private boolean fixedSize = false;
    private float lineHeight;

    private TextPosition horizontal;
    private TextPosition vertical;

    public Textarea(String text, Position position, Color color, Font font) {
        super();
        this.lines = List.of(text.split("\n"));

        this.position = position.copy();
        this.initial = position.copy();

        this.font = font;
        this.color = color;
        this.horizontal = TextPosition.BEGIN;
        this.vertical = TextPosition.BEGIN;
    }

    public void setFixSize(String text, boolean enable) {
        this.setText(text);
        this.fixedSize = enable;
    }

    @Override
    public void setPosition(Position position) {
        super.setPosition(position);
        this.initial = position.copy();
        this.updateText();
    }

    public void setText(String text) {
        this.lines = List.of(text.split("\n"));
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

    public List<String> getTextLines() {
        return this.lines;
    }

    public void setCenter(TextPosition horizontal, TextPosition vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
        this.updateText();
    }

    private void updateText() {
        if (!this.fixedSize) {
            this.size = Application.getContext().getRenderer().getTextSize("X", this.font);
            for (String line : lines) {
                if (line.isEmpty())
                    continue;

                float width = Application.getContext().getRenderer().getTextSize(line, this.font).getWidth();
                if (width > this.size.getWidth()) {
                    this.size.setWidth(width);
                }
            }
            this.lineHeight = this.size.getHeight();
            this.size.setHeight(this.lineHeight * this.lines.size());
        }

        switch (this.horizontal) {
            case CENTER ->
                this.position.setX(this.initial.getX() - this.size.getWidth() / 2);
            case END ->
                this.position.setX(this.initial.getX() - this.size.getWidth());
            default ->
                this.position.setX(this.initial.getX());
        }

        switch (this.vertical) {
            case CENTER ->
                this.position.setY(this.initial.getY() + this.size.getHeight() / 2);
            case END ->
                this.position.setY(this.initial.getY() + this.size.getHeight());
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
    public void update(float dt) {
    }

    @Override
    public void draw() {
        for (int i = 0; i < this.lines.size(); i++) {
            String line = this.lines.get(i);
            Position linePos = Position.of(this.position.getX(), this.position.getY() + i * this.lineHeight);
            Application.getContext().getRenderer().drawText(line, linePos, this.color, this.font);
        }
    }

}
