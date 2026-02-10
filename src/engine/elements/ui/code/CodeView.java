package engine.elements.ui.code;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
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

    private List<HighlightedToken> tokenize() {
        List<HighlightedToken> tokens = new ArrayList<>();

        String current = "";
        int endLineOffset = this.state.getText()
                .getTextPosition(this.scrollOffset + this.maxDisplayLines, Integer.MAX_VALUE).index();

        for (int i = this.scrollOffset; i < endLineOffset; i++) {
            char ch = this.state.getText().getContent().charAt(i);
            if (ch == '\n' || ch == ' ' || ch == '\t') {
                if (!current.isEmpty()) {
                    tokens.add(new HighlightedToken(current, Color.BLACK));
                    current = "";
                }
                tokens.add(new HighlightedToken(String.valueOf(ch), Color.BLACK));
            } else {
                current += ch;
            }
        }
        
        return tokens;
    }

    private void drawView() {

        this.scrollOffset = Math.max(0, this.state.getCursor().getLine()
                - this.maxDisplayLines + CodeState.SCROLL_LINE_GAP);

        List<HighlightedToken> tokens = this.state.getHighlighter().highlight(this.tokenize());

        float lineX = 0.f;
        float lineY = CodeState.LINE_SPACING + this.lineHeight;

        this.view.beginSub();
        for (HighlightedToken token : tokens) {
            if(token.text().equals("\n")) {
                lineX = 0.f;
                lineY += this.lineHeight;
                continue;
            }

            if(token.text().equals("\t")) {
                lineX += CodeState.TEXT_SPACE_SIZE * 2;
                continue;
            }
            if(token.text().equals(" ")) {
                lineX += CodeState.TEXT_SPACE_SIZE;
                continue;
            }

            this.view.drawText(token.text(), Position.of(lineX, lineY), token.color(), this.font);
            lineX += this.view.getTextSize(token.text(), this.font).getWidth();
        }
        lineY += this.lineHeight;
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
