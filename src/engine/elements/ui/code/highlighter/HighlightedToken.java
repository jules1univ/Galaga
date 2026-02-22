package engine.elements.ui.code.highlighter;

import java.awt.Color;

public record HighlightedToken(String text, Color color) {
    public HighlightedToken(HighlightedToken token, Color color) {
        this(token.text(), color);
    }
}
