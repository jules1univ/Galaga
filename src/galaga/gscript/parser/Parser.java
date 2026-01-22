package galaga.gscript.parser;

import galaga.gscript.ast.Program;
import galaga.gscript.lexer.token.TokenStream;

public final class Parser {
    private final TokenStream tokens;

    public Parser(TokenStream tokens) {
        this.tokens = tokens;
    }

    public Program parse() throws ParseException {
        if(this.tokens.isAtEnd()) {
            return new Program();
        }
        
        return null;
    }
}
