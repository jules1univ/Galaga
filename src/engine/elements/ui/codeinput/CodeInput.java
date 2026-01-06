package engine.elements.ui.codeinput;

import engine.Application;
import engine.elements.ui.UIElement;
import engine.graphics.Renderer;
import engine.input.InputKeyListener;
import engine.utils.Pair;
import engine.utils.Position;
import engine.utils.Size;
import galaga.Galaga;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public final class CodeInput extends UIElement {

    private static final float CURSOR_WIDTH = 3f;

    private final Font font;

    private int lineIndex = 0;
    private int colIndex = 0;

    private int maxDisplayLines;

    private boolean focused = false;
    private float cursorBlinkTime = 0.f;
    private boolean cursorBlink = true;
    private Size cursorSize = Size.zero();
    private Position cursorPosition = Position.zero();

    private Renderer viewRenderer;
    private boolean isDirty = true;

    private final SyntaxHighlighter highlighter;
    private final List<String> lines = new ArrayList<>();
    private final List<List<Pair<String, Color>>> highlightedLines = new ArrayList<>();

    private float textBeginX;

    public CodeInput(Position position, Size size, SyntaxHighlighter highlighter, Font font) {
        super();
        this.position = position.copy();
        this.size = size.copy();
        this.font = font;
        this.highlighter = highlighter;
    }

    public void setText(String text) {
        this.highlightedLines.clear();
        this.lines.clear();
        String[] splitLines = text.split("\n");
        for (String line : splitLines) {
            this.lines.add(line);
            this.highlightedLines.add(this.highlighter.highlightLine(line, Color.WHITE));
        }
        this.isDirty = true;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
        if (focused) {
            this.cursorBlink = true;
            this.cursorBlinkTime = 0.3f;
        }
    }

    private void rebuildLinesView() {
        this.viewRenderer.beginSub();

        int lines = Math.min(this.highlightedLines.size(), this.lineIndex + this.maxDisplayLines);
        Size textLineSize = this.viewRenderer.getTextSize(Integer.toString(lines + 1), this.font);

        int lineSpaceHeight = 2;
        int lineSpaceBegin = 10;

        this.textBeginX = lineSpaceBegin + textLineSize.getWidth();

        for (int i = this.lineIndex; i < lines; i++) {
            List<Pair<String, Color>> line = this.highlightedLines.get(i);

            float y = textLineSize.getHeight() * ((i + 1) - this.lineIndex) + (lineSpaceHeight * i);
            this.viewRenderer.drawText(Integer.toString(i + 1),
                    Position.of(this.position.getX(), y),
                    Color.WHITE, this.font);

            int spacing = 0;
            for (Pair<String, Color> word : line) {
                this.viewRenderer.drawText(word.getFirst(),
                        Position.of(this.position.getX() + this.textBeginX + spacing, y),
                        word.getSecond(), this.font);
                spacing += this.viewRenderer.getTextSize(word.getFirst(), this.font).getIntWidth();
            }
        }

        this.viewRenderer.end();
        this.isDirty = false;
    }

    @Override
    public boolean init() {
        int textHeight = Galaga.getContext().getRenderer().getTextSize("X", this.font).getIntHeight();
        this.maxDisplayLines = (int) (this.size.getHeight() / textHeight);

        this.viewRenderer = Renderer.ofSub(this.size);
        this.cursorSize = Size.of(CURSOR_WIDTH, textHeight);
        return true;
    }

    @Override
    public void update(float dt) {
        if (this.isDirty) {
            this.rebuildLinesView();
        }

        if (!this.focused) {
            return;
        }

        boolean moved = false;
        if (Application.getContext().getInput().isKeyDown(KeyEvent.VK_UP)) {
            if (this.lineIndex > 0) {
                this.lineIndex--;
            }
            this.colIndex = Math.min(this.colIndex, this.lines.get(this.lineIndex).length());
            moved = true;
        } else if (Application.getContext().getInput().isKeyDown(KeyEvent.VK_DOWN)) {
            if (this.lineIndex < this.lines.size() - 1) {
                this.lineIndex++;
            }
            this.colIndex = Math.min(this.colIndex, this.lines.get(this.lineIndex).length());
            moved = true;

        } else if (Application.getContext().getInput().isKeyDown(KeyEvent.VK_LEFT)) {
            if (this.colIndex > 0) {
                this.colIndex--;
            }
            moved = true;

        } else if (Application.getContext().getInput().isKeyDown(KeyEvent.VK_RIGHT)) {
            if (this.colIndex < this.lines.get(this.lineIndex).length()) {
                this.colIndex++;
            }
            moved = true;
        }

        if (moved || this.position.isZero()) {
            String cuttedLine = this.lines.get(this.lineIndex).substring(0, this.colIndex);
            Size lineSize;
            if (cuttedLine.isEmpty()) {
                lineSize = this.viewRenderer.getTextSize("X", this.font);
                lineSize.setWidth(0);
            } else {
                lineSize = this.viewRenderer.getTextSize(cuttedLine, this.font);
            }

            this.cursorPosition = Position.of(
                    this.position.getX() + lineSize.getWidth() + this.textBeginX,
                    this.position.getY() + (this.lineIndex - this.lineIndex) * lineSize.getHeight());

        }

        if (this.cursorBlinkTime < 0.f) {
            this.cursorBlinkTime = 0.3f;
            this.cursorBlink = !this.cursorBlink;
        } else {
            this.cursorBlinkTime -= dt;
        }

    }

    @Override
    public void draw(Renderer renderer) {
        renderer.draw(this.viewRenderer, this.position);

        renderer.drawRectOutline(position, size, Color.WHITE);

        if (this.focused && this.cursorBlink) {
            renderer.drawRect(this.cursorPosition, this.cursorSize, Color.WHITE);
        }
    }
}
