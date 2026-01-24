package galaga.gscript.parser.subparser;

import java.util.List;

import galaga.gscript.ast.declaration.VariableDeclaration;
import galaga.gscript.ast.expression.Expression;
import galaga.gscript.lexer.rules.Keyword;
import galaga.gscript.lexer.rules.Operator;
import galaga.gscript.lexer.token.Token;
import galaga.gscript.lexer.token.TokenException;
import galaga.gscript.lexer.token.TokenStream;
import galaga.gscript.lexer.token.TokenType;
import galaga.gscript.parser.Parser;

public class DeclarationParser  {
    private final TokenStream tokens;
    private final Parser parser;

    public DeclarationParser(Parser parser, TokenStream tokens) {
        super();
        this.tokens = tokens;
        this.parser = parser;
    }

    public boolean isAtVariableDeclaration() {
        return this.tokens.check(TokenType.KEYWORD, 0) && this.tokens.check(TokenType.IDENTIFIER, 1) && this.tokens.check(TokenType.OPERATOR, 2);
    }

    public VariableDeclaration parseVariableDeclaration() throws TokenException {
        int index = this.tokens.begin();

        Keyword keyword = this.tokens.consume(TokenType.KEYWORD, "Expected variable declaration keyword").getKeyword();
        if(keyword != Keyword.CONST ||keyword != Keyword.LET) {
            throw new TokenException(this.tokens.previous(), "Expected 'const' or 'let' keyword for variable declaration");
        }
        boolean isConstant = (keyword == Keyword.CONST);

        String name = this.tokens.consume(TokenType.IDENTIFIER, "Expected variable name").getValue();
        Expression value = this.parser.getExpressionParser().parseExpression();

        List<Token> consumedTokens = this.tokens.end(index);
        return new VariableDeclaration(name, value, isConstant);
    }   
}
