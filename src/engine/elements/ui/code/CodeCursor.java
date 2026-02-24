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

    public void setTextIndex(int index) {
        this.textPosition = this.state.getText().getTextPositionFromIndex(index);
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
        } else if (this.textPosition.line() + offset >= this.state.getText().getLineCount()) {
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

        int line = this.textPosition.line() - this.state.getView().getScrollOffset();
        String lineContent = this.state.getText().getLineContent(line);
        String cuttedLine = lineContent.length() <= this.textPosition.column() ? lineContent
                : lineContent.substring(0, this.textPosition.column());
        float lineWidth = 0.f;

        if (!cuttedLine.isEmpty()) {
            cuttedLine = cuttedLine.replace("\t", " ".repeat(2));

            String[] parts = cuttedLine.split(" ", -1);
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                if (!part.isEmpty()) {
                    lineWidth += Application.getContext().getRenderer()
                            .getTextSize(part, this.font)
                            .getWidth();
                }
                if (i < parts.length - 1) {
                    lineWidth += CodeState.TEXT_SPACE_SIZE;
                }
            }
        }

        float lineNumberWidth = Application.getContext().getRenderer()
                .getTextSize(String.valueOf(this.state.getText().getLineCount()), this.font).getWidth()
                + CodeState.LINE_NUMBER_PADDING_LEFT
                + CodeState.LINE_NUMBER_PADDING_RIGHT;

        float x = this.state.getEditor().getPosition().getX()
                + lineNumberWidth
                + lineWidth;

        float y = this.state.getEditor().getPosition().getY()
                + (this.charSize.getHeight() + CodeState.LINE_SPACING) * line
                + this.charSize.getHeight() / 2.f;
        this.position = Position.of(x, y);
    }

    @Override
    public boolean init() {
        this.charSize = Application.getContext().getRenderer().getMaxCharSize(this.font);
        this.size.setHeight(this.charSize.getHeight());
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
        renderer.drawRect(Position.of(
                this.state.getEditor().getPosition().getX(),
                this.position.getY()), Size.of(this.state.getEditor().getSize().getWidth(), this.size.getHeight()),
                new Color(255, 255, 255, 25));
    }

}
