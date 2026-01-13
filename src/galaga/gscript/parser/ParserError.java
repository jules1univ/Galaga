package galaga.gscript.parser;

import galaga.gscript.lexer.token.Token;
import galaga.gscript.lexer.token.TokenType;

public class ParserError extends Exception {
    public ParserError(Token token, TokenType expected, String message) {
        super(String.format("Parser Error at %s: Expected %s but found %s. %s",
                token.getStart().toString(),
                expected.name(),
                token.getType().name(),
                message));
    }

    public ParserError(Token token, TokenType expected, String expectedValue, String message) {
        super(String.format("Parser Error at %s: Expected %s('%s') but found %s('%s'). %s",
                token.getStart().toString(),
                expected.name(),
                expectedValue,
                token.getType().name(),
                token.getValue(),
                message));
    }

}
