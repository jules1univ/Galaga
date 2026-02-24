package engine.elements.ui.code;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import engine.Application;
import engine.elements.ui.UIElement;
import engine.elements.ui.code.highlighter.HighlightedToken;
import engine.graphics.Renderer;
import engine.utils.Position;
import engine.utils.Size;

public class CodeView extends UIElement {

    private final CodeState state;
    private final Font font;
    private Renderer view;

    private boolean isDirty;

    private float lineHeight;

    private int scrollOffset;
    private int maxDisplayLines;

    public CodeView(CodeState state, Font font) {
        this.state = state;
        this.font = font;

        this.size = state.getEditor().getSize();
        this.position = state.getEditor().getPosition();
    }

    public void markDirty() {
        this.isDirty = true;
    }

    public int getScrollOffset() {
        return this.scrollOffset;
    }

    @Override
    public boolean init() {
        this.view = Renderer.ofSub(size);
        this.isDirty = true;

        this.scrollOffset = 0;

        float charSize = Application.getContext().getRenderer().getMaxCharSize(this.font).getHeight();

        this.lineHeight = charSize + CodeState.LINE_SPACING;
        this.maxDisplayLines = (int) Math.floor(this.size.getHeight() / this.lineHeight);

        return true;
    }

    private float drawLineNumbers(int lineStart, int lineEnd) {
        int lineEndPlus = lineEnd + 1;

        String maxLineNumber = String.valueOf(lineEndPlus);
        float maxLineNumberWidth = this.view.getTextSize("X".repeat(maxLineNumber.length()), this.font).getWidth() + CodeState.LINE_NUMBER_PADDING_LEFT;

        float viewY = CodeState.LINE_SPACING + this.lineHeight;
        for (int lineIndex = lineStart; lineIndex < lineEndPlus; lineIndex++) {

            String lineNumber = String.valueOf(lineIndex + 1);
            float lineNumberWidth = this.view.getTextSize("X".repeat(lineNumber.length()), this.font).getWidth();


            this.view.drawText(lineNumber, Position.of(maxLineNumberWidth - lineNumberWidth, viewY), Color.GRAY, this.font);
            viewY += this.lineHeight;
        }

        return maxLineNumberWidth + CodeState.LINE_NUMBER_PADDING_RIGHT;
    }

    private void drawSelection(int lineStart, int lineEnd, float lineNumberWidth) {
        TextPosition selectionStart = this.state.getSelection().getStart();
        TextPosition selectionEnd = this.state.getSelection().getEnd();

        if (selectionStart == null || selectionEnd == null) {
            return;
        }

        if (selectionStart.equals(selectionEnd)) {
            return;
        }

        float viewY = CodeState.LINE_SPACING * 2;
        for (int lineIndex = lineStart; lineIndex < lineEnd; lineIndex++) {

            if (lineIndex >= selectionStart.line() && lineIndex <= selectionEnd.line()) {

                String lineContent = this.state.getText().getLineContent(lineIndex);
                if (lineContent.isEmpty()) {
                    viewY += this.lineHeight;
                    continue;
                }

                int countSpaces = lineContent.length() - lineContent.replace(" ", "").length();
                countSpaces += lineContent.length() - lineContent.replace("\t", "").length();
                countSpaces = Math.max(0, countSpaces - 1);

                Size lineSize = this.view.getTextSize(lineContent, this.font);
                lineSize.setWidth(lineSize.getWidth() + countSpaces * CodeState.TEXT_SPACE_SIZE);

                float viewX = lineNumberWidth;
                if (lineIndex == selectionStart.line()) {
                    String beforeSelection = lineContent.substring(0,
                            selectionStart.column());
                    if (!beforeSelection.isEmpty()) {
                        viewX += this.view.getTextSize(beforeSelection, this.font).getWidth();
                    }
                    lineSize.setWidth(lineSize.getWidth() - viewX);

                    if (selectionStart.line() == selectionEnd.line()) {
                        String inSelection = this.state.getText().getContent(selectionStart, selectionEnd);
                        if (!inSelection.isEmpty()) {
                            lineSize.setWidth(this.view.getTextSize(inSelection, this.font).getWidth());
                        }
                    }
                } else if (lineIndex == selectionEnd.line()) {
                    String beforeSelection = lineContent.substring(0,
                            selectionEnd.column());

                    if (!beforeSelection.isEmpty()) {
                        lineSize.setWidth(this.view.getTextSize(beforeSelection, this.font).getWidth());
                    }
                }

                this.view.drawRect(Position.of(viewX, viewY), lineSize, new Color(0, 120, 215, 100));
            }
            viewY += this.lineHeight;
        }
    }

    private void drawCode(int lineStart, int lineEnd, float lineNumberWidth) {
        List<List<HighlightedToken>> lines = this.state.getHighlighter().highlight(this.state.getText().getContent());
        assert lines.size() == this.state.getText().getLineCount();

        float viewY = CodeState.LINE_SPACING + this.lineHeight;
        for (int lineIndex = lineStart; lineIndex < lineEnd; lineIndex++) {

            List<HighlightedToken> line = lines.get(lineIndex);
            float viewX = lineNumberWidth;

            boolean firstToken = false;
            boolean firstSpace = true;
            boolean switchSpace = false;
            for (HighlightedToken token : line) {

                if (token.text().equals("\t")) {
                    viewX += CodeState.TEXT_SPACE_SIZE * 2;
                    continue;
                } else if (token.text().equals(" ")) {
                    if (!firstToken && !switchSpace && !firstSpace) {
                        this.view.drawLine(
                            Position.of(viewX, viewY - (this.lineHeight - CodeState.LINE_SPACING*2)),
                            Position.of(viewX, viewY),
                            Color.LIGHT_GRAY, 0.5f);
                    }

                    firstSpace = false;
                    switchSpace = !switchSpace;
                    viewX += CodeState.TEXT_SPACE_SIZE;
                    continue;
                }

                this.view.drawText(token.text(), Position.of(viewX, viewY), token.color(), this.font);
                viewX += this.view.getTextSize(token.text(), this.font).getWidth();
                firstToken = true;
            }
            viewY += this.lineHeight;
        }

    }

    private void drawView() {

        int cursorLine = this.state.getCursor().getLine();
        if (cursorLine < this.scrollOffset) {
            this.scrollOffset = cursorLine;
        } else if (cursorLine >= this.scrollOffset + this.maxDisplayLines) {
            this.scrollOffset = cursorLine - this.maxDisplayLines + 1;
        }
        this.scrollOffset = Math.clamp(scrollOffset, 0,
                Math.max(0, this.state.getText().getLineCount() - this.maxDisplayLines));

        int lineStart = this.scrollOffset;
        int lineEnd = Math.min(this.scrollOffset + this.maxDisplayLines, this.state.getText().getLineCount());

        this.view.beginSub();
        
        float lineNumberWidth = this.drawLineNumbers(lineStart, lineEnd);
        this.drawSelection(lineStart, lineEnd, lineNumberWidth);
        this.drawCode(lineStart, lineEnd, lineNumberWidth);

        this.view.end();
    }

    @Override
    public void update(float dt) {
        if (this.isDirty) {
            this.drawView();
            this.isDirty = false;
        }
    }

    @Override
    public void draw(Renderer renderer) {
        renderer.draw(this.view, this.position);
    }

}
