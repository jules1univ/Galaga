package galaga.gscript.parser.subparser;

import java.util.ArrayList;
import java.util.List;

import galaga.gscript.ast.declaration.FunctionDeclaration;
import galaga.gscript.ast.declaration.NativeFunctionDeclaration;
import galaga.gscript.ast.declaration.VariableDeclaration;
import galaga.gscript.ast.expression.Expression;
import galaga.gscript.ast.statement.BlockStatement;
import galaga.gscript.lexer.rules.Keyword;
import galaga.gscript.lexer.rules.Operator;
import galaga.gscript.lexer.token.Token;
import galaga.gscript.lexer.token.TokenException;
import galaga.gscript.lexer.token.TokenRange;
import galaga.gscript.lexer.token.TokenStream;
import galaga.gscript.lexer.token.TokenType;
import galaga.gscript.parser.Parser;

public class DeclarationParser extends SubParser {

    public DeclarationParser(Parser parser, TokenStream tokens) {
        super(parser, tokens);
    }

    public Keyword[] getDeclarationStarters() {
        return new Keyword[] { Keyword.CONST, Keyword.LET, Keyword.FN, Keyword.NATIVE };
    }

    public boolean isAtVariableDeclaration() {
        return this.tokens.check(TokenType.KEYWORD, 0) && this.tokens.check(TokenType.IDENTIFIER, 1)
                && this.tokens.check(TokenType.OPERATOR, 2);
    }

    public boolean isNativeDeclaration() {
        return this.tokens.check(Keyword.NATIVE, 0) && this.tokens.check(TokenType.IDENTIFIER, 1);
    }

    public boolean isFunctionDeclaration() {
        return this.tokens.check(Keyword.FN, 0) && this.tokens.check(TokenType.IDENTIFIER, 1);
    }

    public FunctionDeclaration parseFunctionDeclaration() throws TokenException {
        Token start = this.tokens.current();
        
        this.tokens.consume(Keyword.FN, "Expected 'fn' keyword");
        Token name = this.tokens.consume(TokenType.IDENTIFIER, "Expected function name");

        if (!this.tokens.match(Operator.LEFT_PAREN)) {
            this.parser.report(tokens.current(), "Expected '(' after function name");
        }

        List<Token> parameters = parseParameterList();

        if (!this.tokens.match(Operator.RIGHT_PAREN)) {
            this.parser.report(tokens.current(), "Expected ')' after parameters");
        }

        BlockStatement body = this.parser.getStatementParser().parseBlockStatement();

        Token end = this.tokens.previous();
        return new FunctionDeclaration(name, parameters, body, TokenRange.of(start, end));
    }

    public NativeFunctionDeclaration parseNativeFunctionDeclaration() throws TokenException {
        Token start = this.tokens.current();

        this.tokens.consume(Keyword.NATIVE, "Expected 'native' keyword");
        Token name = this.tokens.consume(TokenType.IDENTIFIER, "Expected native function name");

        if (!this.tokens.match(Operator.LEFT_PAREN)) {
            this.parser.report(tokens.current(), "Expected '(' after native function name");
        }

        List<Token> parameters = parseParameterList();

        if (!this.tokens.match(Operator.RIGHT_PAREN)) {
            this.parser.report(tokens.current(), "Expected ')' after parameters");
        }

        Token end = this.tokens.previous();
        return new NativeFunctionDeclaration(name, parameters, TokenRange.of(start, end));
    }

    public VariableDeclaration parseVariableDeclaration() throws TokenException {
        Token start = this.tokens.current();
        Keyword vardecl = this.tokens.consume(TokenType.KEYWORD, "Expected 'const' or 'let' keyword").getKeyword();
        if (vardecl != Keyword.LET && vardecl != Keyword.CONST) {
            this.parser.report(tokens.current(), "Expected 'const' or 'let' keyword");
        }
        boolean isConstant = (vardecl == Keyword.CONST);
        Token name = this.tokens.consume(TokenType.IDENTIFIER, "Expected variable name");

        if (!this.tokens.match(Operator.ASSIGN)) {
            this.parser.report(tokens.current(), "Expected '=' after variable name");
        }

        Expression value = null;
        try {
            value = this.parser.getExpressionParser().parseExpression();
        } catch (TokenException e) {
            this.parser.report(tokens.current(), e.getMessage());
            this.tokens.advanceUntil(this.getDeclarationStarters());
            return null;
        }

        Token end = this.tokens.previous();
        return new VariableDeclaration(name, value, isConstant, TokenRange.of(start, end));
    }

    private List<Token> parseParameterList() throws TokenException {
        List<Token> parameters = new ArrayList<>();

        while (this.tokens.check(TokenType.IDENTIFIER, 0)) {
            Token param = this.tokens.consume(TokenType.IDENTIFIER, "Expected parameter name");
            parameters.add(param);

            if (this.tokens.check(Operator.COMMA, 0)) {
                this.tokens.consume(Operator.COMMA, "");

                if (!this.tokens.match(TokenType.IDENTIFIER)) {
                    this.parser.report(tokens.current(), "Expected parameter name after ','");
                    break;
                }
            } else {
                break;
            }
        }

        return parameters;
    }
}
