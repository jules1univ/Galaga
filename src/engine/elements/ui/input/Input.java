package engine.elements.ui.input;

import engine.Application;
import engine.elements.ui.UIElement;
import engine.elements.ui.text.Text;
import engine.elements.ui.text.TextPosition;
import engine.graphics.Renderer;
import engine.input.InputKeyListener;
import engine.utils.Position;
import engine.utils.Size;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

public class Input extends UIElement {
    private static final float CURSOR_SIZE = 5.f;
    private static final int NO_MAX_LENGTH = -1;

    private final Font font;

    private String value = "";
    private String displayText = "";
    private final String placeholder;

    private Text text;
    private TextPosition horizontal;
    private TextPosition vertical;

    private boolean focused = false;
    private boolean outline = true;
    private int maxLength = NO_MAX_LENGTH;

    private Color color;

    private int cursor = 0;
    private final Position cursorPosition;
    private float cursorBlinkTime = 0.f;
    private boolean cursorBlink = true;

    public Input(Position position, float width, String placeholder, Color color, Font font) {
        super();
        this.placeholder = placeholder;

        this.font = font;
        this.color = color;

        this.position = position.copy();
        this.size = Size.of(width, 0);

        this.horizontal = TextPosition.BEGIN;
        this.vertical = TextPosition.BEGIN;

        this.cursorPosition = position.copy();
    }

    public void clear() {
        this.value = "";
        this.cursor = 0;
        this.updateText();
    }

    public void setOutline(boolean outline) {
        this.outline = outline;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setText(String text) {
        if (this.maxLength != NO_MAX_LENGTH && text.length() > this.maxLength) {
            text = text.substring(0, this.maxLength);
        }
        this.value = text;
        this.cursor = this.value.length();
        this.updateText();
    }

    public String getText() {
        return this.value;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
        if (focused) {
            this.cursorBlink = true;
            this.cursorBlinkTime = 0.3f;
        }
        this.updateText();
    }

    public boolean isFocused() {
        return this.focused;
    }

    public void setCenter(TextPosition hor, TextPosition ver) {
        this.horizontal = hor;
        this.vertical = ver;
        this.updatePosition();
    }

    private void updateDisplayText(int index, String text) {
        float width = Application.getContext().getRenderer().getTextSize(text, this.font).getWidth();
        if (width + CURSOR_SIZE < this.size.getWidth()) {
            this.displayText = text;
            return;
        }

        if (index > 0) {
            this.updateDisplayText(index - 1, text.substring(1));
        } else {
            this.displayText = text;
        }
    }

    private void updateText() {
        if (this.value.isEmpty()) {
            float width = Application.getContext().getRenderer().getTextSize(this.placeholder, this.font).getWidth();
            if (width + CURSOR_SIZE < this.size.getWidth()) {
                this.displayText = this.placeholder;
            } else {
                for (int i = 0; i < this.placeholder.length(); i++) {
                    String sub = this.placeholder.substring(0, this.placeholder.length() - i);
                    width = Application.getContext().getRenderer().getTextSize(sub, this.font).getWidth();
                    if (width + CURSOR_SIZE < this.size.getWidth()) {
                        this.displayText = sub;
                        break;
                    }
                }
            }

            this.text.setColor(Color.GRAY);
        } else {
            this.updateDisplayText(this.cursor, this.value);
            this.text.setColor(Color.WHITE);
        }
        this.text.setText(this.displayText);

        int padding = 5;
        float width = Application.getContext().getRenderer().getTextSize(this.displayText, this.font).getWidth();
        this.cursorPosition.setX(this.position.getX() + width + padding);
    }

    private void updatePosition() {

        switch (this.horizontal) {
            case BEGIN -> this.position.setX(this.position.getX());
            case CENTER -> this.position.setX(this.position.getX() - this.size.getWidth() / 2.f);
            case END -> this.position.setX(this.position.getX() - this.size.getWidth());
        }

        switch (this.vertical) {
            case BEGIN -> this.position.setY(this.position.getY());
            case CENTER -> this.position.setY(this.position.getY() - this.size.getHeight() / 2.f);
            case END -> this.position.setY(this.position.getY() - this.size.getHeight());
        }

        int padding = 5;
        this.text.setPosition(Position.of(
                this.position.getX() + padding,
                this.position.getY() + padding));
        this.cursorPosition.setY(this.position.getY());
        this.updateText();
    }

    @Override
    public boolean init() {
        int padding = 5;
        this.size.setHeight(
                Application.getContext().getRenderer().getTextSize(this.placeholder, font).getHeight() + padding * 2);
        this.size.setWidth(this.size.getWidth() + padding * 2);

        this.text = new Text("", Position.of(this.position.getX() + padding, this.position.getY() + padding),
                Color.WHITE, this.font);
        if (!this.text.init()) {
            return false;
        }
        this.text.setCenter(TextPosition.BEGIN, TextPosition.END);
        this.updateText();
        return true;
    }

    @Override
    public void update(float dt) {
        if (!this.focused) {
            return;
        }

        if (this.cursorBlinkTime < 0.f) {
            this.cursorBlinkTime = 0.3f;
            this.cursorBlink = !this.cursorBlink;
        } else {
            this.cursorBlinkTime -= dt;
        }

        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_DELETE, KeyEvent.VK_BACK_SPACE)) {
            if (this.value.length() > 0 && this.cursor > 0) {
                this.value = this.value.substring(0, this.cursor - 1) + this.value.substring(this.cursor);
                this.cursor--;
                this.cursorBlink = true;
                this.updateText();
            }
        }

        char ch = Application.getContext().getInput().getTypedChar();
        if (ch != InputKeyListener.NO_CHAR) {
            if (this.maxLength != NO_MAX_LENGTH && this.value.length() + 1 > this.maxLength) {
                return;
            }

            this.value = this.value + String.valueOf(ch);
            this.cursor = this.value.length();
            this.cursorBlink = true;
            this.updateText();
        }

    }

    @Override
    public void draw(Renderer renderer) {
        if (this.outline) {
           renderer.drawRectOutline(this.position, this.size, 2, this.color);
        }

        if (!this.displayText.isEmpty()) {
            this.text.draw(renderer);
        }

        if (this.focused && this.cursorBlink && !this.value.isEmpty()) {
            renderer.drawLine(
                    Position.of(this.cursorPosition.getX(), this.cursorPosition.getY() + this.size.getHeight() / 4.f),
                    Position.of(this.cursorPosition.getX(),
                            this.cursorPosition.getY() + this.size.getHeight() * 3.f / 4.f),
                    this.color, 4);
        }
    }

}
