package engine.elements.ui.codeinput;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import engine.utils.Pair;

public final class SyntaxHighlighter {
    
    private final Map<String, Color> tokens = new HashMap<>();
    private final Map<Pattern, Color> patterns = new HashMap<>();

    public SyntaxHighlighter() {
        
    }

    public void addToken(String token, Color color) {
        this.tokens.put(token, color);
    }

    public void addPattern(Pattern  pattern, Color color) {
        this.patterns.put(pattern, color);
    }

    public void addPattern(String regex, Color color) {
        this.patterns.put(Pattern.compile(regex), color);
    }

    public Pair<String, Color>[] highlightLine(String line , Color defaultColor) {
        String[] tokens = line.split(" ");

        @SuppressWarnings("unchecked")
        Pair<String, Color>[] result = new Pair[tokens.length];
        
        for (int i = 0; i < tokens.length; i++) {
            Color color = this.tokens.getOrDefault(tokens[i], defaultColor);

            if (color == defaultColor) {
                for (Pattern pattern : this.patterns.keySet()) {
                    if (pattern.matcher(tokens[i]).matches()) {
                        color = this.patterns.get(pattern);
                        break;
                    }
                }
            }

            result[i] = Pair.of(tokens[i], color);
        }
        
        return result;
    }
   
}
