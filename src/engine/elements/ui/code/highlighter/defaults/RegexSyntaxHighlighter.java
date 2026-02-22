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
    public List<List<HighlightedToken>> highlight(String content) {
        String[] lines = content.split("\n", -1);

        List<List<HighlightedToken>> highlightedLines = new ArrayList<>();
        for (String line : lines) {
            if (this.cachedLines.containsKey(line)) {
                highlightedLines.add(this.cachedLines.get(line));
                continue;
            }

            List<HighlightedToken> tokens = new ArrayList<>();
            String current = "";
            for (int i = 0; i < line.length(); i++) {
                char ch = line.charAt(i);
                if (ch == '\n' || ch == ' ' || ch == '\t') {
                    if (!current.isEmpty()) {
                        Color color = this.defaultColor;
                        for (RegexHighlightRule rule : this.rules) {
                            if (rule.matches(current)) {
                                color = rule.color();
                                break;
                            }
                        }
                        tokens.add(new HighlightedToken(current, color));
                        current = "";
                    }

                    tokens.add(new HighlightedToken(String.valueOf(ch), this.defaultColor));
                } else {
                    current += ch;
                }
            }
            if (!current.isEmpty()) {
                tokens.add(new HighlightedToken(current, this.defaultColor));
            }
            highlightedLines.add(tokens);
            this.cachedLines.put(line, tokens);
        }
        return highlightedLines;
    }

}
