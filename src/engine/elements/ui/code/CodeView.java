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

    private List<HighlightedToken> tokenize() {
        List<HighlightedToken> tokens = new ArrayList<>();

        String current = "";
        int endLineOffset = this.state.getText()
                .getTextPosition(this.scrollOffset + this.maxDisplayLines, Integer.MAX_VALUE).line();

        for (int lineIndex = this.scrollOffset; lineIndex < endLineOffset; lineIndex++) {
            String line = this.state.getText().getLineContent(lineIndex) + "\n";
            for (int i = 0; i < line.length(); i++) {
                char ch = line.charAt(i);
                if (ch == '\n' || ch == ' ' || ch == '\t') {
                    if (!current.isEmpty()) {
                        tokens.add(new HighlightedToken(current, Color.BLACK, i - current.length(), i));
                        current = "";
                    }
                    tokens.add(new HighlightedToken(String.valueOf(ch), Color.BLACK, i, i + 1));
                } else {
                    current += ch;
                }
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

        TextPosition selStart = this.state.getSelection().getStart();
        TextPosition selEnd = this.state.getCursor().getTextPosition();

        if (selStart != null) {
            if (selStart.index() > selEnd.index()) {
                TextPosition temp = selStart;
                selStart = selEnd;
                selEnd = temp;
            }
        }

        this.view.beginSub();
        for (HighlightedToken token : tokens) {
            if (token.text().equals("\n")) {
                lineX = 0.f;
                lineY += this.lineHeight;
                continue;
            }

            if (token.text().equals("\t")) {
                lineX += CodeState.TEXT_SPACE_SIZE * 2;
                continue;
            }
            if (token.text().equals(" ")) {
                lineX += CodeState.TEXT_SPACE_SIZE;
                continue;
            }

            if (selStart != null && token.startIndex() >= selStart.index()
                    && token.endIndex() <= selEnd.index() + token.text().length()) {
                String selectedText = token.text()
                        .substring(Math.max(0, selStart.index() - token.startIndex()),
                                Math.min(token.text().length(), selEnd.index() - token.startIndex()));

                if (!selectedText.isEmpty()) {

                    this.view.drawRect(Position.of(lineX, lineY - this.lineHeight + CodeState.LINE_SPACING),
                            this.view.getTextSize(selectedText, this.font),
                            new Color(192, 192, 192, 100));
                }
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
