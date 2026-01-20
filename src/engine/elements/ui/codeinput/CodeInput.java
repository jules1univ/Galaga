package engine.elements.ui.codeinput;

import engine.Application;
import engine.elements.ui.UIElement;
import engine.elements.ui.codeinput.highlighter.HighlightedToken;
import engine.elements.ui.codeinput.highlighter.SyntaxHighlighter;
import engine.graphics.Renderer;
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
    private static final float LINE_SPACE_HEIGHT = 0.f;
    private static final float LINE_SPACE_BEGIN = 10.f;

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

    private boolean selectionActive = false;
    private int selectStartLineIndex = -1;
    private int selectStartColumnIndex = -1;

    private Renderer viewRenderer;
    private boolean isViewDirty = true;

    private final SyntaxHighlighter highlighter;
    private final List<String> lines = new ArrayList<>();
    private final List<List<HighlightedToken>> highlightedLines = new ArrayList<>();
    private final List<String> history = new ArrayList<>();

    public CodeInput(Position position, Size size, SyntaxHighlighter highlighter, Font font) {
        super();
        this.position = position.copy();
        this.size = size.copy();
        this.font = font;
        this.highlighter = highlighter;
    }

    public void setText(String text) {
        this.scrollLineIndex = 0;
        this.cursorLineIndex = 0;
        this.cursorColumnIndex = 0;
        this.scrollLineIndex = 0;

        this.resetSelection();

        this.isViewDirty = true;

        this.highlightedLines.clear();
        this.lines.clear();

        String[] splitLines = text.split("\n");
        for (String line : splitLines) {
            this.lines.add(line);
            this.highlightedLines.add(this.highlighter.line(line, Color.WHITE));
        }

        this.updateLineBegin();
    }

    public String getText() {
        return String.join("\n", this.lines);
    }

    public List<String> getLines() {
        return this.lines;
    }

    public String getSelectedText() {
        return String.join("\n", this.getSelectedLines());
    }

    public List<String> getSelectedLines() {
        if (!this.selectionActive) {
            return new ArrayList<>();
        }

        int startLine = Math.min(this.selectStartLineIndex, this.cursorLineIndex);
        int endLine = Math.max(this.selectStartLineIndex, this.cursorLineIndex);

        return this.lines.subList(startLine, endLine + 1);
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
        if (focused) {
            this.cursorBlink = true;
            this.cursorBlinkTime = CURSOR_BLINK_INTERVAL;
        }
    }

    private void updateLineBegin() {
        Size textLineSize = Application.getContext().getRenderer().getTextSize(
                Integer.toString(Math.max(this.maxDisplayLines, this.lines.size())),
                this.font);
        this.lineBegin = LINE_SPACE_BEGIN + textLineSize.getWidth();
    }

    private void rebuildLinesView() {

        this.viewRenderer.beginSub();

        int lineEndIndex = Math.min(this.highlightedLines.size(), this.cursorLineIndex + this.maxDisplayLines);

        int selectStartLine = Math.min(this.selectStartLineIndex, this.cursorLineIndex);
        int selectEndLine = Math.max(this.selectStartLineIndex, this.cursorLineIndex);

        int selectStartColumn = (this.cursorLineIndex == selectStartLine)
                ? this.selectStartColumnIndex
                : 0;
        int selectEndColumn = (this.cursorLineIndex == selectEndLine)
                ? this.cursorColumnIndex
                : this.lines.get(this.cursorLineIndex).length();

        
        float lineSingleNumWidth = this.lineBegin / (int) (Math.log10((double) this.lines.size()) + 1);
        for (int i = this.scrollLineIndex; i < lineEndIndex; i++) {
            List<HighlightedToken> line = this.highlightedLines.get(i);
            boolean selected = this.selectionActive && i >= selectStartLine && i <= selectEndLine;

            int visibleIndex = i - this.scrollLineIndex;
            float y = (visibleIndex + 1) * lineHeight;

            float lineNumX = this.lineBegin - lineSingleNumWidth * ((int) Math.floor(Math.log10((double) (i + 1))) + 1);
            this.viewRenderer.drawText(Integer.toString(i + 1),
                    Position.of(lineNumX / 2.f, y),
                    Color.WHITE, this.font);

            int spacing = 0;
            int columnIndex = 0;
            for (HighlightedToken token : line) {
                if (selected) {
                    columnIndex += token.text().length();
                    int selStart = (i == selectStartLine) ? selectStartColumn : 0;
                    int selEnd = (i == selectEndLine) ? selectEndColumn : this.lines.get(i).length();

                    if (columnIndex > selStart && (columnIndex - token.text().length()) < selEnd) {
                        int startIndex = Math.max(selStart - (columnIndex - token.text().length()), 0);
                        int endIndex = Math.min(selEnd - (columnIndex - token.text().length()),
                                token.text().length());

                        String preSelection = token.text().substring(0, startIndex);
                        String selection = token.text().substring(startIndex, endIndex);

                        float preSelectionWidth = preSelection.isBlank() ? 0
                                : this.viewRenderer.getTextSize(preSelection, this.font).getWidth();
                        float selectionWidth = selection.isBlank() ? 0
                                : this.viewRenderer.getTextSize(selection, this.font).getWidth();

                        this.viewRenderer.drawRect(
                                Position.of(this.lineBegin + spacing + preSelectionWidth,
                                        y - lineHeight + LINE_SPACE_HEIGHT / 2f),
                                Size.of(selectionWidth, lineHeight - LINE_SPACE_HEIGHT),
                                Color.DARK_GRAY);
                    }
                }
                this.viewRenderer.drawText(
                        token.text(),
                        Position.of(this.lineBegin + spacing, y),
                        token.color(),
                        this.font);

                spacing += this.viewRenderer.getTextSize(token.text(), this.font).getIntWidth();
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

        this.updateLineBegin();

        this.cursorSize = Size.of(CURSOR_WIDTH, this.lineHeight - LINE_SPACE_HEIGHT);
        return true;
    }

    private boolean handleSelection() {
        if (Application.getContext().getInput().isKeyDown(KeyEvent.VK_CONTROL)) {
            if (!this.selectionActive) {
                this.selectionActive = true;
                this.selectStartLineIndex = this.cursorLineIndex;
                this.selectStartColumnIndex = this.cursorColumnIndex;
                return true;
            }
        }

        return false;
    }

    private void handleSelectionUpdate() {
        if (this.selectionActive) {
            if (!Application.getContext().getInput().isKeyDown(KeyEvent.VK_CONTROL)) {
                this.resetSelection();
            }

            this.isViewDirty = true;
        }
    }

    private void replaceSelection(String replacement) {
        if (!this.selectionActive) {
            return;
        }

        int startLine = Math.min(this.selectStartLineIndex, this.cursorLineIndex);
        int endLine = Math.max(this.selectStartLineIndex, this.cursorLineIndex);

        int startColumn = (this.cursorLineIndex == startLine)
                ? this.selectStartColumnIndex
                : 0;
        int endColumn = (this.cursorLineIndex == endLine)
                ? this.cursorColumnIndex
                : this.lines.get(this.cursorLineIndex).length();

        if (startLine == endLine) {
            String line = this.lines.get(startLine);
            line = line.substring(0, startColumn) + replacement + line.substring(endColumn);
            this.lines.set(startLine, line);
            this.highlightedLines.set(startLine,
                    this.highlighter.line(line, Color.WHITE));
        } else {
            String firstLine = this.lines.get(startLine);
            String lastLine = this.lines.get(endLine);

            firstLine = firstLine.substring(0, startColumn) + replacement + lastLine.substring(endColumn);
            this.lines.set(startLine, firstLine);
            this.highlightedLines.set(startLine,
                    this.highlighter.line(firstLine, Color.WHITE));

            for (int i = endLine; i > startLine; i--) {
                this.lines.remove(i);
                this.highlightedLines.remove(i);
            }
        }

        this.cursorLineIndex = startLine;
        this.cursorColumnIndex = startColumn + replacement.length();
    }

    private void resetSelection() {
        this.selectionActive = false;
        this.selectStartLineIndex = -1;
        this.selectStartColumnIndex = -1;
    }

    private boolean handleUpDown() {
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_UP)) {
            this.cursorLineIndex--;
            this.handleSelectionUpdate();
            return true;
        } else if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_DOWN)) {
            this.cursorLineIndex++;
            this.handleSelectionUpdate();
            return true;
        }
        return false;
    }

    private boolean handleLeftRight() {
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_LEFT)) {
            this.cursorColumnIndex--;
            if (this.cursorColumnIndex < 0) {
                this.cursorLineIndex = Math.max(0, this.cursorLineIndex - 1);
                this.cursorColumnIndex = this.lines.get(this.cursorLineIndex).length();
            }
            this.handleSelectionUpdate();
            return true;
        } else if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_RIGHT)) {
            this.cursorColumnIndex++;
            if (this.cursorColumnIndex > this.lines.get(this.cursorLineIndex).length()) {
                this.cursorLineIndex = Math.min(this.lines.size() - 1, this.cursorLineIndex + 1);
                this.cursorColumnIndex = 0;
            }
            this.handleSelectionUpdate();
            return true;
        }

        return false;
    }

    private boolean handleTextEnter() {
        if (!Application.getContext().getInput().isTyping()) {
            return false;
        }

        if (this.selectionActive) {
            this.replaceSelection(String.valueOf(Application.getContext().getInput().getTypedChar()));
            this.resetSelection();

            this.isViewDirty = true;
            return true;
        }

        char ch = Application.getContext().getInput().getTypedChar();
        String line = this.lines.get(this.cursorLineIndex);
        line = line.substring(0, this.cursorColumnIndex) + ch + line.substring(this.cursorColumnIndex);

        this.lines.set(this.cursorLineIndex, line);
        this.highlightedLines.set(this.cursorLineIndex,
                this.highlighter.line(line, Color.WHITE));

        this.cursorColumnIndex++;
        this.isViewDirty = true;
        return true;
    }

    private boolean handleTextDelete() {
        if (!Application.getContext().getInput().isKeyPressed(KeyEvent.VK_BACK_SPACE, KeyEvent.VK_DELETE)) {
            return false;
        }

        if (this.selectionActive) {
            this.replaceSelection("");
            this.resetSelection();

            this.isViewDirty = true;
            return true;
        }

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
                    this.highlighter.line(previousLine, Color.WHITE));

            this.lines.remove(this.cursorLineIndex);
            this.highlightedLines.remove(this.cursorLineIndex);

            this.cursorLineIndex--;
            this.cursorColumnIndex = previousLineLength;
        } else if (this.cursorColumnIndex > 0) {
            line = line.substring(0, this.cursorColumnIndex - 1) + line.substring(this.cursorColumnIndex);
            this.lines.set(this.cursorLineIndex, line);
            this.highlightedLines.set(this.cursorLineIndex,
                    this.highlighter.line(line, Color.WHITE));
            this.cursorColumnIndex--;

        }

        this.isViewDirty = true;
        return true;
    }

    private boolean handleTextNewLine() {
        if (!Application.getContext().getInput().isKeyPressed(KeyEvent.VK_ENTER)) {
            return false;
        }

        String line = this.lines.get(this.cursorLineIndex);
        String newLine = line.substring(this.cursorColumnIndex);

        line = line.substring(0, this.cursorColumnIndex);
        this.lines.set(this.cursorLineIndex, line);
        this.highlightedLines.set(this.cursorLineIndex,
                this.highlighter.line(line, Color.WHITE));

        this.lines.add(this.cursorLineIndex + 1, newLine);
        this.highlightedLines.add(this.cursorLineIndex + 1,
                this.highlighter.line(newLine, Color.WHITE));

        this.cursorLineIndex++;
        this.cursorColumnIndex = 0;

        this.isViewDirty = true;
        return true;
    }

    private void handleCopy() {
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_C)
                && Application.getContext().getInput().isKeyDown(KeyEvent.VK_CONTROL)) {
            String selectedText = this.getSelectedText();
            if (!selectedText.isEmpty()) {
                Application.getContext().getClipboard().set(selectedText);
            }
        }
    }

    private void handleCut() {
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_X)
                && Application.getContext().getInput().isKeyDown(KeyEvent.VK_CONTROL)) {
            String selectedText = this.getSelectedText();
            if (!selectedText.isEmpty()) {
                Application.getContext().getClipboard().set(selectedText);
            }

            if (this.selectionActive) {
                int startLine = Math.min(this.selectStartLineIndex, this.cursorLineIndex);
                int endLine = Math.max(this.selectStartLineIndex, this.cursorLineIndex);

                for (int i = endLine; i >= startLine; i--) {
                    this.lines.remove(i);
                    this.highlightedLines.remove(i);
                }

                this.cursorLineIndex = startLine;
                this.cursorColumnIndex = 0;

                this.selectionActive = false;
                this.selectStartLineIndex = -1;
                this.selectStartColumnIndex = -1;

                this.isViewDirty = true;
            }

        }
    }

    private void handlePaste() {
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_V)
                && Application.getContext().getInput().isKeyDown(KeyEvent.VK_CONTROL)) {
            String text = Application.getContext().getClipboard().get();
            if (text == null || text.isEmpty()) {
                return;
            }

            String[] pasteLines = text.split("\n");
            for (int i = 0; i < pasteLines.length; i++) {
                String pasteLine = pasteLines[i];
                if (i == 0) {
                    String line = this.lines.get(this.cursorLineIndex);
                    line = line.substring(0, this.cursorColumnIndex) + pasteLine
                            + line.substring(this.cursorColumnIndex);
                    this.lines.set(this.cursorLineIndex, line);
                    this.highlightedLines.set(this.cursorLineIndex,
                            this.highlighter.line(line, Color.WHITE));
                    this.cursorColumnIndex += pasteLine.length();
                } else {
                    this.cursorLineIndex++;
                    this.lines.add(this.cursorLineIndex, pasteLine);
                    this.highlightedLines.add(this.cursorLineIndex,
                            this.highlighter.line(pasteLine, Color.WHITE));
                    this.cursorColumnIndex = pasteLine.length();
                }
            }

            this.isViewDirty = true;
        }
    }

    private void handleHistory() {
        String text = this.getText();
        if (this.history.isEmpty() || !this.history.get(this.history.size() - 1).equals(text)) {
            this.updateLineBegin();
            this.history.add(text);
        }
    }

    private boolean handleBackHistory() {
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_Z)
                && Application.getContext().getInput().isKeyDown(KeyEvent.VK_CONTROL)) {
            if (this.history.size() < 2) {
                return false;
            }
            this.history.remove(this.history.size() - 1);
            String previousText = this.history.get(this.history.size() - 1);

            int cursorLine = this.cursorLineIndex;
            int cursorColumn = this.cursorColumnIndex;
            int scroolLine = this.scrollLineIndex;

            this.setText(previousText);

            this.cursorLineIndex = Math.min(cursorLine, this.lines.size() - 1);
            this.cursorColumnIndex = Math.min(cursorColumn, this.lines.get(this.cursorLineIndex).length());
            this.scrollLineIndex = scroolLine;

            return true;
        }

        return false;
    }

    @Override
    public void update(float dt) {
        if (this.isViewDirty) {
            this.handleHistory();
            this.rebuildLinesView();
            this.isViewDirty = false;
        }

        if (!this.focused) {
            return;
        }

        boolean moved = this.handleSelection() ||
                this.handleUpDown() ||
                this.handleLeftRight() ||
                this.handleTextEnter() ||
                this.handleTextDelete() ||
                this.handleTextNewLine() ||
                this.handleBackHistory();

        this.handleCopy();
        this.handleCut();
        this.handlePaste();

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
        if (this.focused && this.cursorBlink) {
            renderer.drawRect(this.cursorPosition, this.cursorSize, Color.WHITE);
        }
    }
}
