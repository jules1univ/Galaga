package galaga.gscript.parser.subparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import galaga.gscript.ast.expression.Expression;
import galaga.gscript.ast.expression.IdentifierExpression;
import galaga.gscript.ast.expression.LiteralExpression;
import galaga.gscript.ast.expression.collection.IndexExpression;
import galaga.gscript.ast.expression.collection.ListExpression;
import galaga.gscript.ast.expression.collection.MapExpression;
import galaga.gscript.ast.expression.collection.RangeExpression;
import galaga.gscript.ast.expression.function.CallExpression;
import galaga.gscript.ast.expression.function.FunctionExpression;
import galaga.gscript.ast.expression.operator.BinaryExpression;
import galaga.gscript.ast.expression.operator.UnaryExpression;
import galaga.gscript.ast.statement.BlockStatement;
import galaga.gscript.lexer.rules.Keyword;
import galaga.gscript.lexer.rules.Operator;
import galaga.gscript.lexer.rules.OperatorPriority;
import galaga.gscript.lexer.token.Token;
import galaga.gscript.lexer.token.TokenException;
import galaga.gscript.lexer.token.TokenRange;
import galaga.gscript.lexer.token.TokenStream;
import galaga.gscript.lexer.token.TokenType;
import galaga.gscript.parser.Parser;
import galaga.gscript.types.values.BooleanValue;
import galaga.gscript.types.values.FloatValue;
import galaga.gscript.types.values.IntegerValue;
import galaga.gscript.types.values.NullValue;
import galaga.gscript.types.values.StringValue;
import galaga.gscript.types.values.Value;

public class ExpressionParser extends SubParser {

    public ExpressionParser(Parser parser, TokenStream tokens) {
        super(parser, tokens);
    }

    public Expression parseExpression() throws TokenException {
        return this.parseBinaryExpression(OperatorPriority.MIN_PRIORITY);
    }

    private Expression parseBinaryExpression(int precedence) throws TokenException {
        Expression left = this.parseUnaryExpression();
        while (true) {
            Token operator = tokens.current();
            if (operator.getType() != TokenType.OPERATOR) {
                break;
            }

            int opPrecedence = OperatorPriority.getPriority(operator.getOperator());
            if (opPrecedence < precedence) {
                break;
            }

            tokens.advance();
            Expression right = parseBinaryExpression(opPrecedence + 1);
            left = new BinaryExpression(left, operator, right,
                    TokenRange.of(left.getRange().start(), right.getRange().end()));
        }
        return left;
    }

    private Expression parseUnaryExpression() throws TokenException {
        if (this.tokens.check(TokenType.OPERATOR)) {
            Token operator = this.tokens.current();
            if (OperatorPriority.isUnaryOperator(operator.getOperator())) {
                this.tokens.advance();

                Expression right = this.parseUnaryExpression();
                return new UnaryExpression(right, operator,
                        TokenRange.of(operator, right.getRange().end()));
            }
        }
        return this.parsePrimaryExpression();
    }

    private Expression parsePrimaryExpression() throws TokenException {
        if (this.tokens.check(TokenType.NUMBER) || this.tokens.check(TokenType.STRING)
                || this.tokens.check(Keyword.TRUE) || this.tokens.check(Keyword.FALSE)) {
            return this.parseLiteralExpression();
        } else if (this.tokens.check(Operator.LEFT_BRACKET)) {
            return this.parseListExpression();
        } else if (this.tokens.check(Operator.LEFT_BRACE)) {
            return this.parseMapExpression();
        } else if (this.tokens.check(Operator.LEFT_PAREN)) {
            int func = this.tokens.checkUntil(Operator.ASSIGN);
            if(func != -1 && this.tokens.check(Operator.GREATER_THAN, func+1)) {
                return this.parseFunctionExpression();
            }
            else{
                this.tokens.consume(Operator.LEFT_PAREN, "Expected '('");
                Expression expr = this.parseExpression();
                if (!this.tokens.match(Operator.RIGHT_PAREN)) {
                    this.parser.report(this.tokens.current(), "Expected ')' after expression");
                }
                return expr;
            }
        } else {
            Expression expr = this.parseLiteralExpression();

            while (true) {
                if (this.tokens.check(Operator.DOT) && this.tokens.peek(1).getType() == TokenType.OPERATOR
                        && this.tokens.peek(1).getOperator() == Operator.DOT) {
                    expr = this.parseRangeExpression(expr);
                } else if (this.tokens.check(Operator.LEFT_BRACKET)) {
                    expr = this.parseIndexExpression(expr);
                } else if (this.tokens.check(Operator.LEFT_PAREN)) {
                    expr = this.parseCallExpression(expr);
                } else {
                    break;
                }
            }

            return expr;
        }
    }

    private Expression parseLiteralExpression() {
        if (this.tokens.match(TokenType.IDENTIFIER)) {
            return new IdentifierExpression(this.tokens.previous(), TokenRange.of(this.tokens.previous()));
        }

        Token start = this.tokens.current();
        Value value = new NullValue();

        if (this.tokens.check(TokenType.NUMBER) && this.tokens.check(Operator.DOT, 1)
                && this.tokens.check(TokenType.NUMBER, 2)) {
            try {
                String rawFloat = this.tokens.current().getValue() + "." + this.tokens.peek(2).getValue();
                this.tokens.skip(3);

                value = new FloatValue(Float.parseFloat(rawFloat));
            } catch (NumberFormatException e) {
                this.parser.report(this.tokens.current(), "Invalid float literal");
            }
        } else if (this.tokens.match(TokenType.NUMBER)) {
            try {
                value = new IntegerValue(Integer.parseInt(this.tokens.previous().getValue()));
            } catch (NumberFormatException e) {
                this.parser.report(this.tokens.previous(), "Invalid number literal");
            }
        } else if (this.tokens.match(TokenType.STRING)) {
            value = new StringValue(this.tokens.previous().getValue());
        } else if (this.tokens.check(Keyword.TRUE) || this.tokens.check(Keyword.FALSE)) {
            boolean boolValue = this.tokens.match(Keyword.TRUE);
            value = new BooleanValue(boolValue);
        } else {
            this.parser.report(this.tokens.current(), "Expected literal value");
        }

        Token end = this.tokens.previous();
        return new LiteralExpression(value, TokenRange.of(start, end));
    }

    private ListExpression parseListExpression() throws TokenException {
        Token start = this.tokens.current();
        List<Expression> elements = new ArrayList<>();
        this.tokens.consume(Operator.LEFT_BRACKET, "Expected '[' to start list expression");

        while (!this.tokens.check(Operator.RIGHT_BRACKET) && !this.tokens.isAtEnd()) {
            elements.add(this.parseExpression());
            if (!this.tokens.match(Operator.COMMA)) {
                break;
            }
        }

        if (!this.tokens.match(Operator.RIGHT_BRACKET)) {
            this.parser.report(this.tokens.current(), "Expected ']' to close list expression");
        }

        Token end = this.tokens.previous();
        return new ListExpression(elements, TokenRange.of(start, end));
    }

    private MapExpression parseMapExpression() throws TokenException {
        Token start = this.tokens.current();
        Map<Expression, Expression> entries = new HashMap<>();
        this.tokens.consume(Operator.LEFT_BRACE, "Expected '{' to start map expression");
        while (!this.tokens.check(Operator.RIGHT_BRACE) && !this.tokens.isAtEnd()) {
            Expression key = this.parseExpression();
            if (!this.tokens.match(Operator.COLON)) {
                this.parser.report(this.tokens.current(), "Expected ':' after map key");
            }
            Expression value = this.parseExpression();
            entries.put(key, value);
            if (!this.tokens.match(Operator.COMMA)) {
                break;
            }
        }
        if (!this.tokens.match(Operator.RIGHT_BRACE)) {
            this.parser.report(this.tokens.current(), "Expected '}' to close map expression");
        }

        Token end = this.tokens.previous();
        return new MapExpression(entries, TokenRange.of(start, end));
    }

    private FunctionExpression parseFunctionExpression() throws TokenException {
        Token start = this.tokens.current();
        List<Token> parameters = new ArrayList<>();

        this.tokens.consume(Operator.LEFT_PAREN, "Expected '(' for function parameters");

        while (!this.tokens.check(Operator.RIGHT_PAREN) && !this.tokens.isAtEnd()) {
            Token param = this.tokens.consume(TokenType.IDENTIFIER, "Expected parameter name");
            parameters.add(param);

            if (!this.tokens.match(Operator.COMMA)) {
                break;
            }
        }

        if (!this.tokens.match(Operator.RIGHT_PAREN)) {
            this.parser.report(this.tokens.current(), "Expected ')' after function parameters");
        }

        BlockStatement body = this.parser.getStatementParser().parseBlockStatement();

        Token end = this.tokens.previous();
        return new FunctionExpression(parameters, body, TokenRange.of(start, end));
    }

    private RangeExpression parseRangeExpression(Expression startExpr) throws TokenException {
        this.tokens.consume(Operator.DOT, "Expected '..' in range expression");
        this.tokens.consume(Operator.DOT, "Expected '..' in range expression");
        Expression endExpr = this.parseExpression();

        Token end = this.tokens.previous();
        return new RangeExpression(startExpr, endExpr, TokenRange.of(startExpr.getRange().start(), end));
    }

    private IndexExpression parseIndexExpression(Expression target) throws TokenException {
        this.tokens.consume(Operator.LEFT_BRACKET, "Expected '[' for index expression");
        Expression index = this.parseExpression();
        if (!this.tokens.match(Operator.RIGHT_BRACKET)) {
            this.parser.report(this.tokens.current(), "Expected ']' to close index expression");
        }
        Token end = this.tokens.previous();
        return new IndexExpression(target, index, TokenRange.of(target.getRange().start(), end));
    }

    private CallExpression parseCallExpression(Expression callee) throws TokenException {
        List<Expression> arguments = new ArrayList<>();
        this.tokens.consume(Operator.LEFT_PAREN, "Expected '(' for function call");

        while (!this.tokens.check(Operator.RIGHT_PAREN) && !this.tokens.isAtEnd()) {
            arguments.add(this.parseExpression());
            if (!this.tokens.match(Operator.COMMA)) {
                break;
            }
        }

        if (!this.tokens.match(Operator.RIGHT_PAREN)) {
            this.parser.report(this.tokens.current(), "Expected ')' to close function call");
        }

        Token end = this.tokens.previous();
        return new CallExpression(callee, arguments, TokenRange.of(callee.getRange().start(), end));
    }

}
