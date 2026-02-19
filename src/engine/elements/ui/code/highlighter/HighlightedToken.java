package engine.elements.ui.code.highlighter;

import java.awt.Color;

public record HighlightedToken(String text, Color color, int startIndex, int endIndex) {
    public HighlightedToken(HighlightedToken token, Color color) {
        this(token.text(), color, token.startIndex(), token.endIndex());
    }
}
