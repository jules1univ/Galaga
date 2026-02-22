package engine.elements.ui.code;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import engine.Application;
import engine.elements.ui.UIElement;
import engine.elements.ui.code.highlighter.HighlightedToken;
import engine.graphics.Renderer;
import engine.utils.Position;

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

        this.maxDisplayLines = (int) Math.floor(this.size.getHeight() / charSize);
        this.lineHeight = charSize + CodeState.LINE_SPACING;

        return true;
    }

    private void drawView() {
        this.scrollOffset = Math.max(0, this.state.getCursor().getLine()
                - this.maxDisplayLines + CodeState.SCROLL_LINE_GAP);

        List<List<HighlightedToken>> lines = this.state.getHighlighter().highlight(this.state.getText().getContent());

        TextPosition selectionStart = this.state.getSelection().getStart();
        TextPosition selectionEnd = this.state.getSelection().getEnd();

        this.view.beginSub();

        float lineY = CodeState.LINE_SPACING + this.lineHeight;
        int index = 0;
        for (List<HighlightedToken> line : lines) {
            float lineX = 0.f;
            for (HighlightedToken token : line) {
                if (token.text().equals("\n")) {
                    lineX = 0.f;
                    lineY += this.lineHeight;
                    index++;
                    continue;
                }

                if (token.text().equals("\t")) {
                    lineX += CodeState.TEXT_SPACE_SIZE * 2;
                    index++;
                    continue;
                }

                if (token.text().equals(" ")) {
                    lineX += CodeState.TEXT_SPACE_SIZE;
                    index++;
                    continue;
                }

                if (selectionStart != null  && selectionEnd != null) {
                    TextPosition tokenPosition = this.state.getText().getTextPositionFromIndex(index);
                    
                    if(tokenPosition.isInRange(selectionStart, selectionEnd)) {
                        String cuttedText = token.text().substring(0, Math.min(token.text().length(), selectionEnd.index() - tokenPosition.index()));
                        this.view.drawRect(Position.of(lineX, lineY - this.lineHeight + CodeState.LINE_SPACING),
                                this.view.getTextSize(cuttedText, this.font),
                                new Color(192, 192, 192, 100));
                    }
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
