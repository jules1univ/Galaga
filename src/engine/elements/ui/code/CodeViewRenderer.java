package engine.elements.ui.code;

import engine.elements.ui.code.highlighter.HighlightedToken;
import engine.elements.ui.code.selection.SelectRange;
import engine.graphics.Renderer;
import engine.utils.Position;
import engine.utils.Size;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

public class CodeViewRenderer {
    private CodeContext context;
    private Renderer renderer;
    private Font font;

    private boolean isDirty = true;
    private float lineBegin;
    private float lineHeight;

    public CodeViewRenderer(){

    }

    public void init(CodeContext context, Size size, Font font, float lineHeight) {
        this.context = context;
        this.font = font;
        this.lineHeight = lineHeight;

        this.renderer = Renderer.ofSub(size);
    }

    public void setLineBegin(float lineBegin) {
        this.lineBegin = lineBegin;
    }

    public void markDirty() {
        this.isDirty = true;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void rebuild() {
        this.renderer.beginSub();

        SelectRange range = this.context.selection().range();
        float lineSingleNumWidth = this.lineBegin / (int) (Math.log10((double) this.context.lines().size()) + 1);

        for (int i = this.context.cursor().getScrollStartLine(); i < this.context.cursor().getScrollEndLine(); i++) {
            List<HighlightedToken> line = this.context.highlighter().get(i);

            int visibleIndex = i - this.context.cursor().getScrollStartLine();
            float y = (visibleIndex + 1) * lineHeight;

            drawLineNumber(i, y, lineSingleNumWidth);

            if (range != null && i >= range.start().line() && i <= range.end().line()) {
                drawLineWithSelection(line, this.context.text().get(i), i, y, range);
            } else {
                drawLine(line, y);
            }
        }

        this.renderer.end();
        this.isDirty = false;
    }

    private void drawLineNumber(int lineIndex, float y, float lineSingleNumWidth) {
        float lineNumX = this.lineBegin
                - lineSingleNumWidth * ((int) Math.floor(Math.log10((double) (lineIndex + 1))) + 1);
        this.renderer.drawText(Integer.toString(lineIndex + 1),
                Position.of(lineNumX / 2.f, y),
                Color.WHITE, this.font);
    }

    private void drawLine(List<HighlightedToken> line, float y) {
        int spacing = 0;
        for (HighlightedToken token : line) {
            this.renderer.drawText(
                    token.text(),
                    Position.of(this.lineBegin + spacing, y),
                    token.color(),
                    this.font);

            spacing += this.renderer.getTextSize(token.text(), this.font).getIntWidth();
        }
    }

    private void drawLineWithSelection(List<HighlightedToken> line, String lineText,
            int lineIndex, float y,
            SelectRange range) {
        int spacing = 0;
        int columnIndex = 0;

        int selStart = (lineIndex == range.start().line()) ? range.start().column() : 0;
        int selEnd = (lineIndex == range.end().line()) ? range.end().column() : lineText.length();

        for (HighlightedToken token : line) {
            columnIndex += token.text().length();

            if (columnIndex > selStart && (columnIndex - token.text().length()) < selEnd) {
                int startIndex = Math.max(selStart - (columnIndex - token.text().length()), 0);
                int endIndex = Math.min(selEnd - (columnIndex - token.text().length()),
                        token.text().length());

                String preSelection = token.text().substring(0, startIndex);
                String selection = token.text().substring(startIndex, endIndex);

                float preSelectionWidth = preSelection.isBlank() ? 0
                        : this.renderer.getTextSize(preSelection, this.font).getWidth();
                float selectionWidth = selection.isBlank() ? 0
                        : this.renderer.getTextSize(selection, this.font).getWidth();

                this.renderer.drawRect(
                        Position.of(this.lineBegin + spacing + preSelectionWidth,
                                y - lineHeight + CodeEditor.LINE_SPACE_HEIGHT / 2.f),
                        Size.of(selectionWidth, lineHeight - CodeEditor.LINE_SPACE_HEIGHT),
                        Color.DARK_GRAY);
            }

            this.renderer.drawText(
                    token.text(),
                    Position.of(this.lineBegin + spacing, y),
                    token.color(),
                    this.font);

            spacing += this.renderer.getTextSize(token.text(), this.font).getIntWidth();
        }
    }

    public void draw(Renderer renderer, Position position) {
        renderer.draw(this.renderer, position);
    }
}