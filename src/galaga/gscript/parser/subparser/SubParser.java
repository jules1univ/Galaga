package galaga.gscript.parser.subparser;

import galaga.gscript.lexer.token.TokenStream;
import galaga.gscript.parser.Parser;

public abstract class SubParser {
    protected final Parser parser;
    protected final TokenStream tokens;

    public SubParser(Parser parser, TokenStream tokens) {
        super();
        this.parser = parser;
        this.tokens = tokens;
    }


}
