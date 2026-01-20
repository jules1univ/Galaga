package engine.elements.ui.codeinput.highlighter.defaults;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import engine.elements.ui.codeinput.highlighter.HighlightedToken;
import engine.elements.ui.codeinput.highlighter.SyntaxHighlighter;

public final class RegexSyntaxHighlighter implements SyntaxHighlighter {
    private final List<RegexHighlightRule> rules = new ArrayList<>();

    public RegexSyntaxHighlighter() {
    }

    public void addPattern(String regex, Color color) {
        rules.add(new RegexHighlightRule(regex, color));
    }

    public List<HighlightedToken> line(String line, Color defaultColor) {

        List<HighlightedToken> result = new ArrayList<>();
        int index = 0;

        while (index < line.length()) {

            boolean matched = false;

            for (RegexHighlightRule rule : rules) {
                Matcher matcher = rule.pattern.matcher(line);
                matcher.region(index, line.length());

                if (matcher.lookingAt()) {
                    String token = matcher.group();
                    result.add(new HighlightedToken(token, rule.color));
                    index += token.length();
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                result.add(new HighlightedToken(
                        String.valueOf(line.charAt(index)),
                        defaultColor
                ));
                index++;
            }
        }

        return result;
    }
}
