package engine.elements.ui.codeinput;

import engine.Application;
import engine.elements.ui.UIElement;
import engine.graphics.Renderer;
import engine.utils.Pair;
import engine.utils.Position;
import engine.utils.Size;
import engine.utils.logger.Log;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public final class CodeInput extends UIElement {

    private static final float CURSOR_WIDTH = 3f;
    private final int LINE_SPACE_HEIGHT = 2;
    private final int LINE_SPACE_BEGIN = 10;

    private final Font font;

    private int maxDisplayLines;
    private int scrollLineIndex = 0;

    private float lineBegin;
    private float lineHeight;

    private boolean focused = false;

    private float cursorBlinkTime = 0.f;
    private boolean cursorBlink = true;

    private Size cursorSize = Size.zero();
    private Position cursorPosition = Position.zero();

    private int cursorLineIndex = 0;
    private int cursorColumnIndex = 0;

    private Renderer viewRenderer;

    private final SyntaxHighlighter highlighter;
    private final List<String> lines = new ArrayList<>();
    private final List<List<Pair<String, Color>>> highlightedLines = new ArrayList<>();

    public CodeInput(Position position, Size size, SyntaxHighlighter highlighter, Font font) {
        super();
        this.position = position.copy();
        this.size = size.copy();
        this.font = font;
        this.highlighter = highlighter;
    }

    public void setText(String text) {
        this.cursorLineIndex = 0;
        this.cursorColumnIndex = 0;
        this.scrollLineIndex = 0;

        this.highlightedLines.clear();
        this.lines.clear();

        String[] splitLines = text.split("\n");
        for (String line : splitLines) {
            this.lines.add(line);
            this.highlightedLines.add(this.highlighter.highlightLine(line, Color.WHITE));
        }

        Size textLineSize =  Application.getContext().getRenderer().getTextSize(Integer.toString(this.maxDisplayLines), this.font);
        this.lineBegin = this.LINE_SPACE_BEGIN + textLineSize.getWidth();

        this.rebuildLinesView();

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

        int lineEndIndex = Math.min(this.highlightedLines.size(), this.cursorLineIndex + this.maxDisplayLines);
        for (int i = this.scrollLineIndex; i < lineEndIndex; i++) {
            List<Pair<String, Color>> line = this.highlightedLines.get(i);

            int visibleIndex = i - this.scrollLineIndex;
            float y = (visibleIndex + 1) * lineHeight;

            this.viewRenderer.drawText(Integer.toString(i + 1),
                    Position.of(0, y),
                    Color.WHITE, this.font);

            int spacing = 0;
            for (Pair<String, Color> word : line) {

                this.viewRenderer.drawText(
                        word.getFirst(),
                        Position.of(this.lineBegin + spacing, y),
                        word.getSecond(),
                        this.font);

                spacing += this.viewRenderer.getTextSize(word.getFirst(), this.font).getIntWidth();
            }
        }

        this.viewRenderer.end();
    }

    @Override
    public boolean init() {
        this.lineHeight = Application.getContext().getRenderer().getMaxCharSize(this.font).getHeight()
                + this.LINE_SPACE_HEIGHT;
        this.maxDisplayLines = Math.floorDiv((int) this.size.getHeight(),
                (int) this.lineHeight) - 1;

        this.viewRenderer = Renderer.ofSub(this.size);

        Size textLineSize =  Application.getContext().getRenderer().getTextSize(Integer.toString(this.maxDisplayLines), this.font);
        this.lineBegin = this.LINE_SPACE_BEGIN + textLineSize.getWidth();

        this.cursorSize = Size.of(CURSOR_WIDTH, this.lineHeight - this.LINE_SPACE_HEIGHT);
        return true;
    }

    @Override
    public void update(float dt) {
        if (!this.focused) {
            return;
        }

        boolean moved = false;
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_UP)) {
            this.cursorLineIndex--;
            moved = true;
        } else if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_DOWN)) {
            this.cursorLineIndex++;
            moved = true;
        }

        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_LEFT)) {
            this.cursorColumnIndex--;
            moved = true;
        } else if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_RIGHT)) {
            this.cursorColumnIndex++;
            moved = true;
        }

        if (moved || this.cursorPosition.isZero()) {
            this.cursorBlink = true;
            this.cursorLineIndex = Math.clamp(this.cursorLineIndex, 0, this.lines.size() - 1);
            this.cursorColumnIndex = Math.clamp(this.cursorColumnIndex, 0,
                    this.lines.get(this.cursorLineIndex).length());
            
            if (this.cursorLineIndex < this.scrollLineIndex) {
                this.scrollLineIndex = this.cursorLineIndex;
            } else if (this.cursorLineIndex >= this.scrollLineIndex + this.maxDisplayLines) {
                this.scrollLineIndex = this.cursorLineIndex - this.maxDisplayLines + 1;
            }

            int visibleLine = this.cursorLineIndex - this.scrollLineIndex;

            String cuttedLine = lines.get(this.cursorLineIndex).substring(0, this.cursorColumnIndex);
            float cuttedWidth = cuttedLine.isBlank() ? 0f : viewRenderer.getTextSize(cuttedLine, font).getWidth();

            this.cursorPosition = Position.of(
                    this.position.getX() + this.lineBegin + cuttedWidth,
                    this.position.getY() + (visibleLine) * this.lineHeight + this.LINE_SPACE_HEIGHT/2.f);

            this.rebuildLinesView();
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
