package engine.elements.ui.loading;

import java.awt.Color;
import java.awt.Font;

import engine.elements.ui.Alignment;
import engine.elements.ui.UIElement;
import engine.elements.ui.text.Text;
import engine.graphics.Renderer;
import engine.utils.Position;
import engine.utils.Size;

public class Loading extends UIElement {

    private final Color color;
    private final Color oppositeColor;

    private float percent;
    private boolean customText = false;

    private Position initialPosition;

    private final int padding;
    private Position innerPosition;
    private Size innerSize;

    private Text text;
    private Font font;

    public Loading(Position position, Size size, int padding, Color color, Font font) {
        super();
        this.padding = padding;
        this.percent = 0.0f;

        this.initialPosition = position.copy();
        this.position = position.copy();
        this.size = size.copy();

        this.color = color;
        this.oppositeColor = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
        this.font = font;
    }

    
    public int getPadding() {
        return this.padding;
    }

    public void setCustomText(String text) {
        this.customText = text != null && !text.isEmpty();
        if (this.customText) {
            this.text.setText(text);
        }
    }

    public void setFont(Font font) {
        this.font = font;
        this.text.setFont(font);
    }

    public void setPercent(float percent) {
        this.percent = percent;
        this.innerSize.setWidth((this.size.getWidth() - this.padding * 2) * this.percent);

        if (this.customText) {
            return;
        }
        this.text.setText(String.format("%.0f%%", this.percent * 100));
    }

    public void setCenter(Alignment horizontal, Alignment vertical) {
        switch (horizontal) {
            case BEGIN -> this.position.setX(this.initialPosition.getX());
            case CENTER -> this.position.setX(this.initialPosition.getX() - this.size.getWidth() / 2);
            case END -> this.position.setX(this.initialPosition.getX() - this.size.getWidth());
        }

        switch (vertical) {
            case BEGIN -> this.position.setY(this.initialPosition.getY());
            case CENTER -> this.position.setY(this.initialPosition.getY() - this.size.getHeight() / 2);
            case END -> this.position.setY(this.initialPosition.getY() - this.size.getHeight());
        }
        this.updateInner();
        this.text.setPosition(Position.of(
                this.position.getX() + this.size.getWidth() / 2,
                this.position.getY() + this.size.getHeight() / 2));
    }

    private void updateInner() {
        this.innerPosition = Position.of(
                this.position.getX() + this.padding,
                this.position.getY() + this.padding);

        this.innerSize = Size.of(
                (this.size.getWidth() - this.padding * 2) * this.percent,
                this.size.getHeight() - this.padding * 2);
    }

    @Override
    public boolean init() {
        this.updateInner();
        Position textPos = Position.of(
                this.position.getX() + this.size.getWidth() / 2,
                this.position.getY() + this.size.getHeight() / 2);
        this.text = new Text(String.format("%.0f%%", this.percent * 100), textPos, this.color, this.font);
        if (!this.text.init()) {
            return false;
        }
        this.text.setCenter(Alignment.CENTER, Alignment.CENTER);
        return true;
    }

    @Override
    public void update(float dt) {
    }

    @Override
    public void draw(Renderer renderer) {
        renderer.drawRectOutline(this.position, this.size, this.padding, this.color);
        renderer.drawRect(this.innerPosition, this.innerSize, this.oppositeColor);
        this.text.draw(renderer);
    }


}
