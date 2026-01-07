package engine.elements.ui.codeinput;

import engine.Application;
import engine.elements.ui.UIElement;
import engine.graphics.Renderer;
import engine.utils.Pair;
import engine.utils.Position;
import engine.utils.Size;
import engine.utils.logger.Log;
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
    private boolean isViewDirty = true;

    private final SyntaxHighlighter highlighter;
    private final List<String> lines = new ArrayList<>();
    private final List<List<Pair<String, Color>>> highlightedLines = new ArrayList<>();

    private float textBeginX;
    private final int lineSpaceHeight = 2;
    private final int lineSpaceBegin = 10;

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
        this.isViewDirty = true;
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

        this.textBeginX = lineSpaceBegin + textLineSize.getWidth();

        for (int i = this.lineIndex; i < lines; i++) {
            List<Pair<String, Color>> line = this.highlightedLines.get(i);

            int index = ((i + 1) - this.lineIndex);
            float y = (lineSpaceHeight + textLineSize.getHeight()) * index;

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
        this.isViewDirty = false;
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
        if (this.isViewDirty) {
            this.rebuildLinesView();
        }

        if (!this.focused) {
            return;
        }

        boolean moved = false;
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_UP)) {
            this.lineIndex--;
            moved = true;
        } else if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_DOWN)) {
            this.lineIndex++;
            moved = true;
        }

        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_LEFT)) {
            this.colIndex--;
            moved = true;
        } else if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_RIGHT)) {
            this.colIndex++;
            moved = true;
        }

        if (moved || this.cursorPosition.isZero()) {
            this.lineIndex = Math.clamp(this.lineIndex, 0, this.maxDisplayLines);
            this.colIndex = Math.clamp(this.colIndex, 0, this.lines.get(this.lineIndex).length());

            String cuttedLine = this.lines.get(this.lineIndex).substring(0, this.colIndex);
            Size lineSize;
            if (cuttedLine.isEmpty()) {
                lineSize = Application.getContext().getRenderer().getTextSize("X", this.font);
                lineSize.setWidth(0);
            } else {
                lineSize = Application.getContext().getRenderer().getTextSize(cuttedLine, this.font);
            }

            this.cursorPosition = Position.of(
                    this.position.getX() + lineSize.getWidth() + this.textBeginX,
                    this.position.getY() + this.lineIndex * ((lineSize.getHeight() + this.lineSpaceHeight)));
                    
            this.cursorBlink = true;
            this.isViewDirty = true;
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
