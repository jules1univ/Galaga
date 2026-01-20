package engine.elements.ui.codeinput.highlighter;

import java.awt.Color;
import java.util.List;

public interface SyntaxHighlighter {
    public List<HighlightedToken> line(String line, Color defaultColor);
}
