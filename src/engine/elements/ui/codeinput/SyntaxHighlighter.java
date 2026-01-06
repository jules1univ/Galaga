package engine.elements.ui.codeinput;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import engine.utils.Pair;

public final class SyntaxHighlighter {

    private final List<SyntaxRule> rules = new ArrayList<>();

    public void addPattern(String regex, Color color) {
        rules.add(new SyntaxRule(regex, color));
    }

    public List<Pair<String, Color>> highlightLine(String line, Color defaultColor) {

        List<Pair<String, Color>> result = new ArrayList<>();
        int index = 0;

        while (index < line.length()) {

            boolean matched = false;

            for (SyntaxRule rule : rules) {
                Matcher matcher = rule.pattern.matcher(line);
                matcher.region(index, line.length());

                if (matcher.lookingAt()) {
                    String token = matcher.group();
                    result.add(Pair.of(token, rule.color));
                    index += token.length();
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                result.add(Pair.of(
                        String.valueOf(line.charAt(index)),
                        defaultColor
                ));
                index++;
            }
        }

        return result;
    }
}
