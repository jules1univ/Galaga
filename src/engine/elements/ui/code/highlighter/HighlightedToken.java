package engine.elements.ui.code.highlighter;

import java.awt.Color;

public record HighlightedToken(String text, Color color, HighlightStyle style) {

    public HighlightedToken(String text, Color color) {
        this(text, color, new HighlightStyle(HighlightStyleType.DEFAULT, color));
    }

    public HighlightedToken(HighlightedToken token, Color color) {
        this(token.text(), color, token.style());
    }
}
