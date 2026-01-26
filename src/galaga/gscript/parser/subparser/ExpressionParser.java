package galaga.gscript.parser.subparser;

import galaga.gscript.ast.expression.Expression;
import galaga.gscript.lexer.token.TokenException;
import galaga.gscript.lexer.token.TokenStream;
import galaga.gscript.parser.Parser;

public class ExpressionParser extends SubParser {


    public ExpressionParser(Parser parser, TokenStream tokens) {
        super(parser, tokens);
    }

    public Expression parseExpression() throws TokenException {
        return null;
    }
}
