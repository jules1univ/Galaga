package engine.elements.ui.code.highlighter;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;

public abstract class SyntaxHighlighter {
    protected final Color defaultColor;
    protected final HashMap<String, List<HighlightedToken>> cachedLines = new HashMap<>();

    public SyntaxHighlighter(Color defaultColor) {
        this.defaultColor = defaultColor;
    }

    public abstract SyntaxHighlightResult highlight(String content);
}
