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
            String[] parts = line.split(" ", -1);
            for (String part : parts) {
                if(part.isEmpty()) {
                    part = " ";
                }else if(part.length() == 1 && part.charAt(0) == '\t') {
                    part = " ".repeat(2);
                }

                Color color = this.defaultColor;
                for (RegexHighlightRule rule : this.rules) {
                    if (rule.matches(part)) {
                        color = rule.color();
                        break;
                    }
                }
                tokens.add(new HighlightedToken(part, color));
                tokens.add(new HighlightedToken(" ", this.defaultColor));
            }

            highlightedLines.add(tokens);
            this.cachedLines.put(line, tokens);
        }
        return highlightedLines;
    }

}
