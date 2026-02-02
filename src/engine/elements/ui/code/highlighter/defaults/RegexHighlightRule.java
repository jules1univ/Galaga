package engine.elements.ui.code.highlighter.defaults;

import java.awt.Color;
import java.util.regex.Pattern;

public record RegexHighlightRule(Pattern pattern, Color color) {
    public RegexHighlightRule(String regex, Color color) {
        this(Pattern.compile(regex), color);
    }
}