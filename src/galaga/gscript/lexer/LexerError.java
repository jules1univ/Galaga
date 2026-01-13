package galaga.gscript.lexer;

import galaga.gscript.lexer.token.TokenPosition;

public class LexerError extends Exception {
    public LexerError(TokenPosition position, String value, String message) {
        super(String.format("Lexer error at line %d, column %d: %s (value: '%s')",
                position.getLine(), position.getColumn(), message, value));
    }
    
}
