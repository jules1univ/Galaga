package engine.elements.ui.codeinput.highlighter.defaults;

import java.awt.Color;
import java.util.regex.Pattern;

public final class RegexHighlightRule {
    public final Pattern pattern;
    public final Color color;

    public RegexHighlightRule(String regex, Color color) {
        this.pattern = Pattern.compile(regex);
        this.color = color;
    }
}

