package engine.elements.ui.code.highlighter;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public abstract class SyntaxHighlighter {
    protected final Color defaultColor;
    protected final List<List<HighlightedToken>> lines = new ArrayList<>();

    public SyntaxHighlighter(Color defaultColor) {
        this.defaultColor = defaultColor;
    }

    public abstract void update(List<String> newLines);

    public abstract void update(int lineIndex, String line);

    public final List<HighlightedToken> get(int lineIndex) {
        return lineIndex >= 0 && lineIndex < lines.size() ? lines.get(lineIndex) : List.of();
    }
}
