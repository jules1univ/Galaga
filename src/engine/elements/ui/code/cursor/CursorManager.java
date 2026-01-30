package engine.elements.ui.code.cursor;

import engine.Application;
import engine.elements.ui.code.CodeContext;
import engine.elements.ui.code.CodeEditor;
import engine.graphics.Renderer;
import engine.utils.Position;
import engine.utils.Size;

import java.awt.Color;
import java.awt.Font;

public final class CursorManager {
    public static final float CURSOR_WIDTH = 3f;
    public static final float CURSOR_BLINK_INTERVAL = 0.5f;

    private final CodeContext context;
    private final Font font;

    private float blinkTime = 0.f;
    private boolean blink = true;

    private Size size = Size.zero();
    private Position displayPosition = Position.zero();
    private final Position basePosition;

    private TextPosition textPosition = TextPosition.start();
    private int textScrollLine = 0;

    private int maxDisplayLines;
    private float lineBegin;
    private float lineHeight;

    public CursorManager(CodeEditor codeInput, Font font, Position basePosition) {
        this.context = codeInput.getContext();
        this.font = font;
        this.basePosition = basePosition;
    }

    public void init(int maxDisplayLines, float lineBegin, float lineHeight) {
        this.size = Size.of(CURSOR_WIDTH, lineHeight - CodeEditor.LINE_SPACE_HEIGHT);
        this.maxDisplayLines = maxDisplayLines;
        this.lineBegin = lineBegin;
        this.lineHeight = lineHeight;
    }

    public void setLineBegin(float lineBegin) {
        this.lineBegin = lineBegin;
    }

    public void resetBlink() {
        this.blink = true;
        this.blinkTime = CURSOR_BLINK_INTERVAL;
    }

    public void resetPosition() {
        this.textPosition = TextPosition.start();
    }

    public void resetScroll() {
        this.textScrollLine = 0;
    }

    public TextPosition getPosition() {
        return this.textPosition;
    }

    public int getScrollStartLine() {
        return this.textScrollLine;
    }

    public int getScrollEndLine() {
        return this.textScrollLine + this.maxDisplayLines;
    }

    public void setPosition(TextPosition position) {
        this.textPosition = position;
        this.updatePosition();
    }

    public void startColumn() {
        this.textPosition = new TextPosition(
                this.textPosition.line(),
                0);
        this.updatePosition();
    }

    public void endColumn() {
        this.textPosition = new TextPosition(
                this.textPosition.line(),
                this.context.text().get(this.textPosition.line()).length());
        this.updatePosition();

    }

    public void moveLine(int lineDelta) {
        int line = Math.clamp(
                this.textPosition.line() + lineDelta,
                0,
                this.context.lines().size() - 1);

        this.textPosition = new TextPosition(
                line,
                this.textPosition.column());
        this.updatePosition();

    }

    public void moveColumn(int columnDelta) {
        int column = Math.clamp(
                this.textPosition.column() + columnDelta,
                0,
                this.context.text().get(this.textPosition.line()).length());
        this.textPosition = new TextPosition(
                this.textPosition.line(),
                column);
        this.updatePosition();
    }

    public boolean shouldDrawCursor() {
        return blink;
    }

    private void updatePosition() {

        this.resetBlink();
        this.textPosition = new TextPosition(
                Math.clamp(this.textPosition.line(), 0, this.context.lines().size() - 1),
                Math.clamp(this.textPosition.column(), 0, this.context.text().get(this.textPosition.line()).length()));

        int visibleLine = Math.clamp(
                this.textPosition.line() - this.textScrollLine,
                0,
                maxDisplayLines - 1);

        String cuttedLine = this.context.text().get(this.textPosition.line()).substring(0, this.textPosition.column());
        float cuttedWidth = cuttedLine.isBlank() ? 0f
                : Application.getContext().getRenderer().getTextSize(cuttedLine, font).getWidth();

        this.displayPosition = Position.of(
                basePosition.getX() + lineBegin + cuttedWidth,
                basePosition.getY() + (visibleLine * lineHeight) + (CodeEditor.LINE_SPACE_HEIGHT / 2.f));

        int cursorLine = this.textPosition.line();
        if (cursorLine < this.textScrollLine) {
            this.textScrollLine = cursorLine;
            this.context.view().markDirty();
        } else if (cursorLine >= this.textScrollLine + this.maxDisplayLines) {
            this.textScrollLine = cursorLine - this.maxDisplayLines + 1;
            this.context.view().markDirty();
        }
    }

    public void update(float dt) {
        if (this.blinkTime < 0.f) {
            this.blinkTime = CURSOR_BLINK_INTERVAL;
            this.blink = !this.blink;
        } else {
            this.blinkTime -= dt;
        }
    }

    public void draw(Renderer renderer) {
        renderer.drawRect(this.displayPosition, this.size, Color.WHITE);
    }
}