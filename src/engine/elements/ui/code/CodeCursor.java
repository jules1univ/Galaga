package engine.elements.ui.code;

import java.awt.Color;
import java.awt.Font;

import engine.Application;
import engine.elements.ui.UIElement;
import engine.graphics.Renderer;
import engine.utils.Position;
import engine.utils.Size;

public class CodeCursor extends UIElement {
    private final CodeState state;
    private final Font font;
    private Size charSize;

    private boolean blink;
    private float blinkTime;

    private TextPosition textPosition;

    public CodeCursor(CodeState state, Font font) {
        this.state = state;
        this.font = font;

        this.position = state.getEditor().getPosition();
        this.size = Size.of(CodeState.CURSOR_WIDTH, 0.f);

        this.blink = true;
        this.blinkTime = CodeState.CURSOR_BLINK_INTERVAL;

        this.textPosition = TextPosition.of(0, 0, 0);
    }

    public TextPosition getTextPosition() {
        return this.textPosition;
    }

    public void setTextPosition(TextPosition textPosition) {
        this.textPosition = textPosition;
        this.updatePosition();
    }

    public int getLine() {
        return this.textPosition.line();
    }

    public void moveLine(int offset) {
        if (this.textPosition.line() + offset < 0) {
            this.textPosition = TextPosition.of(0, this.textPosition.column(), this.textPosition.index());
            this.updatePosition();

            return;
        } else if (this.textPosition.line() + offset >= this.state.getText().getLines().size()) {
            this.textPosition = this.state.getText().getTextPosition(
                    Integer.MAX_VALUE,
                    this.textPosition.column());
            this.updatePosition();

            return;
        }

        this.textPosition = this.state.getText().getTextPosition(
                this.textPosition.line() + offset,
                this.textPosition.column());
        this.updatePosition();

    }

    public int getColumn() {
        return this.textPosition.column();
    }

    public void moveColumn(int offset) {
        if (this.textPosition.column() + offset < 0) {
            this.moveLine(-1);
            this.endColumn();
            return;
        } else if (this.textPosition.column() + offset > this.state.getText().getLineLength(this.textPosition.line())) {
            this.moveLine(1);
            this.beginColumn();
            return;
        }

        this.textPosition = TextPosition.of(
                this.textPosition.line(),
                this.textPosition.column() + offset,
                this.textPosition.index() + offset);
        this.updatePosition();
    }

    public void beginColumn() {
        this.textPosition = TextPosition.of(
                this.textPosition.line(),
                0,
                this.textPosition.index() - this.textPosition.column());
        this.updatePosition();

    }

    public void endColumn() {
        this.textPosition = this.state.getText().getTextPosition(
                this.textPosition.line(),
                Integer.MAX_VALUE);
        this.updatePosition();

    }

    public void resetBlink() {
        this.blink = true;
        this.blinkTime = CodeState.CURSOR_BLINK_INTERVAL;
    }

    private void updatePosition() {
        this.resetBlink();

        String cuttedLine = this.state.getText().getLineContent(this.textPosition.line()).substring(0,
                this.textPosition.column());
        float lineWidth = 0.f;
        if (!cuttedLine.isEmpty()) {
            lineWidth = Application.getContext().getRenderer()
                    .getTextSize(cuttedLine, this.font)
                    .getWidth();
        }

        float x = this.state.getEditor().getPosition().getX()
                + CodeState.LINE_NUMBER_PADDING
                + lineWidth;

        float y = this.state.getEditor().getPosition().getY()
                + (this.charSize.getHeight() + CodeState.LINE_SPACING) * this.textPosition.line();

        this.position = Position.of(x, y);
        this.size.setHeight(this.charSize.getHeight());
    }

    @Override
    public boolean init() {
        this.charSize = Application.getContext().getRenderer().getMaxCharSize(this.font);
        this.updatePosition();
        return true;
    }

    @Override
    public void update(float dt) {
        if (this.blinkTime < 0.f) {
            this.blinkTime = CodeState.CURSOR_BLINK_INTERVAL;
            this.blink = !this.blink;
        } else {
            this.blinkTime -= dt;
        }
    }

    @Override
    public void draw(Renderer renderer) {
        if (this.blink) {
            renderer.drawRect(this.position, this.size, Color.WHITE);
        }
    }

}
