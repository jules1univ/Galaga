package galaga.gscript.parser;

import galaga.gscript.lexer.token.Token;

public record ParseError(Token token, String message) {

    public String displayContext(String source) {
        String[] lines = source.split("\n");
        int lineNumber = token.getStart().getLine();
        if (lineNumber < 1 || lineNumber > lines.length) {
            return "";
        }
        String line = lines[lineNumber - 1];
        StringBuilder pointerLine = new StringBuilder();
        for (int i = 0; i < token.getStart().getColumn() - 1; i++) {
            pointerLine.append(' ');
        }
        pointerLine.append('^');
        pointerLine.append(" -- ").append(message);
        return line + "\n" + pointerLine.toString();
    }
}
