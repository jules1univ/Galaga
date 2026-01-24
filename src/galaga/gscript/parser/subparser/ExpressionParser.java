package galaga.gscript.parser.subparser;

import galaga.gscript.ast.expression.Expression;
import galaga.gscript.lexer.token.TokenStream;

public class ExpressionParser  {

    private final TokenStream tokens;

    public ExpressionParser(TokenStream tokens) {
        super();
        this.tokens = tokens;
    }

    public Expression parseExpression() {
        return null;
    }
}
