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

    private void drawView() {

        int cursorLine = this.state.getCursor().getLine();
        if (cursorLine < this.scrollOffset) {
            this.scrollOffset = cursorLine;
        } else if (cursorLine >= this.scrollOffset + this.maxDisplayLines) {
            this.scrollOffset = cursorLine - this.maxDisplayLines + 1;
        }
        this.scrollOffset = Math.clamp(scrollOffset, 0,
                Math.max(0, this.state.getText().getLineCount() - this.maxDisplayLines));

        List<List<HighlightedToken>> lines = this.state.getHighlighter().highlight(this.state.getText().getContent());

        TextPosition selectionStart = this.state.getSelection().getStart();
        TextPosition selectionEnd = this.state.getSelection().getEnd();

        this.view.beginSub();

        int index = 0;
        float lineY = CodeState.LINE_SPACING + this.lineHeight;

        for (int i = this.scrollOffset; i < lines.size(); i++) {

            List<HighlightedToken> line = lines.get(i);
            float lineX = 0.f;

            for (HighlightedToken token : line) {
                if (selectionStart != null && selectionEnd != null) {
                    TextPosition tokenPosition = this.state.getText().getTextPositionFromIndex(index);
                    if (tokenPosition.isInRange(selectionStart, selectionEnd)) {
                        Size textSize = Size.zero();
                        if (token.text().equals("\t") || token.text().equals(" ")) {
                            int lineLength = this.state.getText().getLineLength(tokenPosition.line());
                            if (lineLength > 0) {
                                if (token.text().equals("\t")) {
                                    textSize = Size.of(CodeState.TEXT_SPACE_SIZE * 2,
                                            this.lineHeight - CodeState.LINE_SPACING);
                                } else if (token.text().equals(" ")) {
                                    textSize = Size.of(CodeState.TEXT_SPACE_SIZE,
                                            this.lineHeight - CodeState.LINE_SPACING);
                                }
                            }
                        } else {
                            int endLength = Math.min(token.text().length(), selectionEnd.index() - tokenPosition.index());
                            String cuttedText = this.state.getText().getContent(tokenPosition.index(), endLength);
                            textSize = this.view.getTextSize(cuttedText, this.font);
                        }

                        if (!textSize.equals(Size.zero())) {
                            this.view.drawRect(Position.of(lineX, lineY - this.lineHeight + CodeState.LINE_SPACING),
                                    textSize,
                                    new Color(192, 192, 192, 100));
                        }
                    }
                }

                if (token.text().equals("\t")) {
                    lineX += CodeState.TEXT_SPACE_SIZE * 2;
                    index++;
                    continue;
                } else if (token.text().equals(" ")) {
                    lineX += CodeState.TEXT_SPACE_SIZE;
                    index++;
                    continue;
                }

                this.view.drawText(token.text(), Position.of(lineX, lineY), token.color(), this.font);
                lineX += this.view.getTextSize(token.text(), this.font).getWidth();
                index += token.text().length();
            }
            lineY += this.lineHeight;
            index++;
        }

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
