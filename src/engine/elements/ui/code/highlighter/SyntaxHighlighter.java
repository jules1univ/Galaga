package engine.elements.ui.code.highlighter;

import java.awt.Color;
import java.util.List;

public abstract class SyntaxHighlighter {
    protected final Color defaultColor;

    public SyntaxHighlighter(Color defaultColor) {
        this.defaultColor = defaultColor;
    }

    public abstract List<HighlightedToken> highlight(List<HighlightedToken> tokens);
}
