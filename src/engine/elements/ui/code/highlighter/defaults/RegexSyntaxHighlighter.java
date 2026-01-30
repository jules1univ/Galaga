package engine.elements.ui.code.highlighter.defaults;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import engine.elements.ui.code.highlighter.HighlightedToken;
import engine.elements.ui.code.highlighter.SyntaxHighlighter;

public final class RegexSyntaxHighlighter extends SyntaxHighlighter {
    private final List<RegexHighlightRule> rules = new ArrayList<>();

    public RegexSyntaxHighlighter(Color defaultColor) {
        super(defaultColor);
    }

    public void addPattern(String regex, Color color) {
        rules.add(new RegexHighlightRule(regex, color));
    }

    @Override
    public void update(List<String> newLines) {
        this.lines.clear();
        for (String line : newLines) {
            List<HighlightedToken> result = new ArrayList<>();
            int index = 0;

            while (index < line.length()) {

                boolean matched = false;

                for (RegexHighlightRule rule : rules) {
                    var matcher = rule.pattern.matcher(line);
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
                            defaultColor));
                    index++;
                }
            }

            this.lines.add(result);
        }
    }

    @Override
    public void update(int lineIndex, String line) {
        List<HighlightedToken> result = new ArrayList<>();
        int index = 0;

        while (index < line.length()) {

            boolean matched = false;

            for (RegexHighlightRule rule : rules) {
                var matcher = rule.pattern.matcher(line);
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
                        defaultColor));
                index++;
            }
        }

        if (lineIndex >= 0 && lineIndex < lines.size()) {
            this.lines.set(lineIndex, result);
        } else if (lineIndex == lines.size()) {
            this.lines.add(result);
        }
    }

}
