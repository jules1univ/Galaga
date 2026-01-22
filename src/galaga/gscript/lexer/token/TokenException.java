package galaga.gscript.lexer.token;

public final class TokenException extends Exception {
    private final Token token;

    public TokenException(Token token, String message) {
        super(message);
        this.token = token;
    }
    
    public Token getToken() {
        return token;
    }
}
