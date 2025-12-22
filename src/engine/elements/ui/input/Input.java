package engine.elements.ui.input;

import java.awt.Color;
import java.awt.Font;

import engine.elements.ui.UIElement;
import engine.elements.ui.text.Text;
import engine.elements.ui.text.TextPosition;
import engine.utils.Position;
import engine.utils.Size;
import galaga.Galaga;

public class Input extends UIElement {
    private static final float CURSOR_SIZE = 5.f;
    private static final int NO_MAX_LENGTH = -1;

    private final String placeholder;
    private final Font font;

    private int maxLength = NO_MAX_LENGTH;

    private Color color;
    private Color textColor;

    private String value = "";
    private String displayText = "";

    private Text text;

    private boolean focused = false;
    private int cursor = 0;

    private Position cursorPosition;
    private float cursorBlinkTime = 0.f;
    private boolean cursorBlink = true;

    public Input(Position position, float width, String placeholder, Color color, Font font) {
        super();
        this.placeholder = placeholder;

        this.font = font;
        this.color = color;
        this.textColor = Color.GRAY;

        this.position = position;
        this.size = Size.of(width, 0);
        this.cursorPosition = Position.of(position.getX(), position.getY());
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setValue(String text) {
        if(this.maxLength != NO_MAX_LENGTH && text.length() > this.maxLength) {
            text = text.substring(0, this.maxLength);
        }
        this.value = text;
        this.updateText();
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
        if (Galaga.getContext().getInput().isTyping() && !focused) {
            Galaga.getContext().getInput().stopTyping();
        }
    }

    private void updateDisplayText(int index, String text) {
        float width = Galaga.getContext().getRenderer().getTextSize(text, this.font).getWidth();
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
            this.updateDisplayText(0, this.placeholder);
            this.textColor = Color.GRAY;
        } else {
            this.updateDisplayText(this.cursor, this.value);
            this.textColor = Color.WHITE;
        }
        this.text.setText(this.displayText);

        int margin = 5;
        float width = Galaga.getContext().getRenderer().getTextSize(this.displayText, this.font).getWidth();
        this.cursorPosition.setX(this.position.getX() + width + margin);
    }

    @Override
    public boolean init() {
        int margin = 5;
        this.size.setHeight(
                Galaga.getContext().getRenderer().getTextSize(this.placeholder, font).getHeight() + margin * 2);
        this.size.setWidth(this.size.getWidth() + margin * 2);

        this.text = new Text("", Position.of(this.position.getX() + margin, this.position.getY() + margin),
                this.textColor, this.font);
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
        if (!Galaga.getContext().getInput().isTyping()) {
            Galaga.getContext().getInput().startTyping();
        }

        String newValue = Galaga.getContext().getInput().getTypedText();
        if (!newValue.equals(this.value)) {
            if(this.maxLength != NO_MAX_LENGTH && newValue.length() > this.maxLength) {
                newValue = newValue.substring(0, this.maxLength);
            }
            this.value = newValue;

            this.cursor = this.value.length();
            this.cursorBlink = true;
            this.updateText();
        }

        if (this.cursorBlinkTime < 0.f) {
            this.cursorBlinkTime = 0.3f;
            this.cursorBlink = !this.cursorBlink;
        } else {
            this.cursorBlinkTime -= dt;
        }
    }

    @Override
    public void draw() {
        Galaga.getContext().getRenderer().drawRectOutline(this.position, this.size, 2, this.color);
        if (!this.displayText.isEmpty()) {
            this.text.draw();
        }

        if (this.focused && this.cursorBlink && !this.value.isEmpty()) {
            Galaga.getContext().getRenderer().drawLine(
                    Position.of(this.cursorPosition.getX(), this.cursorPosition.getY() + this.size.getHeight() / 4.f),
                    Position.of(this.cursorPosition.getX(),
                            this.cursorPosition.getY() + this.size.getHeight() * 3.f / 4.f),
                    this.color, 2);
        }
    }

}
