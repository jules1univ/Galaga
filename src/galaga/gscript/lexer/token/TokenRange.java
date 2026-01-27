package galaga.gscript.lexer.token;

public record TokenRange(Token start, Token end) {

    public static TokenRange of(Token token) {
        return new TokenRange(token, token);
    }

    public static TokenRange of(Token start, Token end) {
        return new TokenRange(start, end);
    }

    public static TokenRange empty() {
        return new TokenRange(null, null);
    }

    public TokenPosition getStart() {
        return start.getStart();
    }

    public TokenPosition getEnd() {
        return end.getEnd();
    }
}
