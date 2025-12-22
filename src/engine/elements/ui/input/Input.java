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

    private final String placeholder;
    private final Font font;

    private Color color;
    private Color textColor;

    private String value = "";
    private String displayText = "";

    private Text text;

    private boolean focused = false;
    private int cursor = 0;

    public Input(Position position, float width, String placeholder, Color color, Font font) {
        super();
        this.placeholder = placeholder;

        this.font = font;
        this.color = color;
        this.textColor = Color.GRAY;

        this.position = position;
        this.size = Size.of(width, 0);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setValue(String text) {
        this.value = text;
        this.updateText();
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
        if (Galaga.getContext().getInput().isTyping() && !focused) {
            Galaga.getContext().getInput().stopTyping();
        }
    }

    private void updateDisplayText(int start, String text) {
        float totalWidth = 0.f;
        for (int i = start; i < text.length(); i++) {
            String ch = String.valueOf(text.charAt(i));
            float width = Galaga.getContext().getRenderer().getTextSize(ch, font).getWidth();
            if (totalWidth + width > this.size.getWidth()) {
                this.displayText = text.substring(0, i);
                break;
            }
            totalWidth += width;
        }
        if (totalWidth <= this.size.getWidth()) {
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
            this.value = newValue;
            this.updateText();
        }
    }

    @Override
    public void draw() {
        Galaga.getContext().getRenderer().drawRectOutline(this.position, this.size, 2, this.color);
        this.text.draw();

        if (this.focused) {
        }
    }

}
