package galaga.gscript.parser;

import galaga.gscript.lexer.token.Token;

public record ParseError(Token token, String message, ParseErrorLevel level) {
}
