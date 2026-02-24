package engine.elements.ui.code.highlighter.defaults;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import engine.elements.ui.code.highlighter.HighlightedToken;
import engine.elements.ui.code.highlighter.SyntaxHighlightResult;
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
    public SyntaxHighlightResult highlight(String content) {
        String[] lines = content.split("\n", -1);

        List<List<HighlightedToken>> highlightedLines = new ArrayList<>();
        for (String line : lines) {
            if (this.cachedLines.containsKey(line)) {
                highlightedLines.add(this.cachedLines.get(line));
                continue;
            }

            List<HighlightedToken> tokens = new ArrayList<>();
            if (!line.isEmpty()) {
                String token = "";
                for (char ch : line.toCharArray()) {
                    if (ch == ' ') {
                        if (!token.isEmpty()) {
                            Color color = this.defaultColor;
                            for (RegexHighlightRule rule : this.rules) {
                                if (rule.matches(token)) {
                                    color = rule.color();
                                    break;
                                }
                            }
                            tokens.add(new HighlightedToken(token, color));
                            token = "";
                        }

                        tokens.add(new HighlightedToken(" ", this.defaultColor));
                    } else {
                        token += ch;
                    }
                }

                if (!token.isEmpty()) {
                    Color color = this.defaultColor;
                    for (RegexHighlightRule rule : this.rules) {
                        if (rule.matches(token)) {
                            color = rule.color();
                            break;
                        }
                    }
                    tokens.add(new HighlightedToken(token, color));
                }
            }

            highlightedLines.add(tokens);
            this.cachedLines.put(line, tokens);
        }
        return new SyntaxHighlightResult(highlightedLines);
    }

}
