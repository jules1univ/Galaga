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
    public List<HighlightedToken> highlight(List<HighlightedToken> tokens) {
        List<HighlightedToken> highlightedTokens = new ArrayList<>();
        for (HighlightedToken token : tokens) {

            boolean matched = false;
            for (RegexHighlightRule rule : rules) {
                if (rule.pattern().matcher(token.text()).matches()) {
                    highlightedTokens.add(new HighlightedToken(token.text(), rule.color()));
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                highlightedTokens.add(new HighlightedToken(token.text(), defaultColor));
            }
        }
        return highlightedTokens;
    }

}
