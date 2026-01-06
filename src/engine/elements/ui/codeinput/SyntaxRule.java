package engine.elements.ui.codeinput;

import java.awt.Color;
import java.util.regex.Pattern;

public final class SyntaxRule {
    public final Pattern pattern;
    public final Color color;

    public SyntaxRule(String regex, Color color) {
        this.pattern = Pattern.compile(regex);
        this.color = color;
    }
}
