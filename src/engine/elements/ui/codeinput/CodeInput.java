package engine.elements.ui.codeinput;

import engine.Application;
import engine.elements.ui.UIElement;
import engine.graphics.Renderer;
import engine.utils.Pair;
import engine.utils.Position;
import engine.utils.Size;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public final class CodeInput extends UIElement {

    private static final float CURSOR_WIDTH = 3f;
    private static final float CURSOR_BLINK_INTERVAL = 0.5f;
    private static final int LINE_SPACE_HEIGHT = 2;
    private static final int LINE_SPACE_BEGIN = 10;

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
    private boolean isViewDirty = true;

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
        this.isViewDirty = true;

        this.highlightedLines.clear();
        this.lines.clear();

        String[] splitLines = text.split("\n");
        for (String line : splitLines) {
            this.lines.add(line);
            this.highlightedLines.add(this.highlighter.highlightLine(line, Color.WHITE));
        }

        Size textLineSize = Application.getContext().getRenderer().getTextSize(Integer.toString(this.maxDisplayLines),
                this.font);
        this.lineBegin = LINE_SPACE_BEGIN + textLineSize.getWidth();
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
        if (focused) {
            this.cursorBlink = true;
            this.cursorBlinkTime = CURSOR_BLINK_INTERVAL;
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
                + LINE_SPACE_HEIGHT;
        this.maxDisplayLines = Math.floorDiv((int) this.size.getHeight(),
                (int) this.lineHeight) - 1;

        this.viewRenderer = Renderer.ofSub(this.size);

        Size textLineSize = Application.getContext().getRenderer().getTextSize(Integer.toString(this.maxDisplayLines),
                this.font);
        this.lineBegin = LINE_SPACE_BEGIN + textLineSize.getWidth();

        this.cursorSize = Size.of(CURSOR_WIDTH, this.lineHeight - LINE_SPACE_HEIGHT);
        return true;
    }

    @Override
    public void update(float dt) {
        if (this.isViewDirty) {
            this.rebuildLinesView();
            this.isViewDirty = false;
        }

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

        if (Application.getContext().getInput().isTyping()) {
            char ch = Application.getContext().getInput().getTypedChar();
            String line = this.lines.get(this.cursorLineIndex);
            line = line.substring(0, this.cursorColumnIndex) + ch + line.substring(this.cursorColumnIndex);
            this.lines.set(this.cursorLineIndex, line);
            this.highlightedLines.set(this.cursorLineIndex,
                    this.highlighter.highlightLine(line, Color.WHITE));

            moved = true;
            this.cursorColumnIndex++;
            this.isViewDirty = true;
        }

        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_BACK_SPACE, KeyEvent.VK_DELETE)) {
            String line = this.lines.get(this.cursorLineIndex);

            if (this.cursorColumnIndex == 0 && line.isEmpty() && this.lines.size() > 1) {
                this.lines.remove(this.cursorLineIndex);
                this.highlightedLines.remove(this.cursorLineIndex);

                this.cursorColumnIndex = 0;
                this.cursorLineIndex = Math.max(0, this.cursorLineIndex - 1);
            } else if (this.cursorColumnIndex == 0 && this.cursorLineIndex > 0) {
                String previousLine = this.lines.get(this.cursorLineIndex - 1);
                int previousLineLength = previousLine.length();
                previousLine += line;

                this.lines.set(this.cursorLineIndex - 1, previousLine);
                this.highlightedLines.set(this.cursorLineIndex - 1,
                        this.highlighter.highlightLine(previousLine, Color.WHITE));

                this.lines.remove(this.cursorLineIndex);
                this.highlightedLines.remove(this.cursorLineIndex);

                this.cursorLineIndex--;
                this.cursorColumnIndex = previousLineLength;
            } else if (this.cursorColumnIndex > 0) {
                line = line.substring(0, this.cursorColumnIndex - 1) + line.substring(this.cursorColumnIndex);
                this.lines.set(this.cursorLineIndex, line);
                this.highlightedLines.set(this.cursorLineIndex,
                        this.highlighter.highlightLine(line, Color.WHITE));
                this.cursorColumnIndex--;

            }
            moved = true;
            this.isViewDirty = true;

        }

        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_ENTER)) {
            String line = this.lines.get(this.cursorLineIndex);
            String newLine = line.substring(this.cursorColumnIndex);

            line = line.substring(0, this.cursorColumnIndex);
            this.lines.set(this.cursorLineIndex, line);
            this.highlightedLines.set(this.cursorLineIndex,
                    this.highlighter.highlightLine(line, Color.WHITE));

            this.lines.add(this.cursorLineIndex + 1, newLine);
            this.highlightedLines.add(this.cursorLineIndex + 1,
                    this.highlighter.highlightLine(newLine, Color.WHITE));

            this.cursorLineIndex++;
            this.cursorColumnIndex = 0;

            moved = true;
            this.isViewDirty = true;
        }

        if (moved || this.cursorPosition.isZero()) {
            this.cursorBlink = true;
            this.cursorLineIndex = Math.clamp(this.cursorLineIndex, 0, this.lines.size() - 1);
            this.cursorColumnIndex = Math.clamp(this.cursorColumnIndex, 0,
                    this.lines.get(this.cursorLineIndex).length());

            if (this.cursorLineIndex < this.scrollLineIndex) {
                this.scrollLineIndex = this.cursorLineIndex;
                this.isViewDirty = true;
            } else if (this.cursorLineIndex >= this.scrollLineIndex + this.maxDisplayLines) {
                this.scrollLineIndex = this.cursorLineIndex - this.maxDisplayLines + 1;
                this.isViewDirty = true;
            }

            int visibleLine = this.cursorLineIndex - this.scrollLineIndex;

            String cuttedLine = lines.get(this.cursorLineIndex).substring(0, this.cursorColumnIndex);
            float cuttedWidth = cuttedLine.isBlank() ? 0f : viewRenderer.getTextSize(cuttedLine, font).getWidth();

            this.cursorPosition = Position.of(
                    this.position.getX() + this.lineBegin + cuttedWidth,
                    this.position.getY() + (visibleLine * this.lineHeight) + (LINE_SPACE_HEIGHT / 2.f));

        }

        if (this.cursorBlinkTime < 0.f) {
            this.cursorBlinkTime = CURSOR_BLINK_INTERVAL;
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
