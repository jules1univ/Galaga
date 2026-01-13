package galaga.gscript.lexer.token;

public final class Token {
    private final TokenPosition start;
    private final TokenPosition end;

    private final TokenType type;
    private final String value;

    public static Token of(TokenType type, TokenPosition start, String value) {
        return new Token(type, start, TokenPosition.of(start.getLine(), start.getColumn() + value.length()), value);
    }

    private Token(TokenType type, TokenPosition start, TokenPosition end, String value) {
        this.type = type;
        this.start = start;
        this.end = end;
        this.value = value;
    }

    public TokenPosition getStart() {
        return start;
    }

    public TokenPosition getEnd() {
        return end;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Token{" +
                "start=" + start +
                ", end=" + end +
                ", type=" + type +
                ", value='" + value + '\'' +
                '}';
    }
}
