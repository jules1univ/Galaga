package galaga.gscript.parser.subparser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import galaga.gscript.ast.expression.Expression;
import galaga.gscript.ast.statement.AssignmentStatement;
import galaga.gscript.ast.statement.BlockStatement;
import galaga.gscript.ast.statement.ExpressionStatement;
import galaga.gscript.ast.statement.ReturnStatement;
import galaga.gscript.ast.statement.Statement;
import galaga.gscript.ast.statement.logic.IfStatement;
import galaga.gscript.ast.statement.logic.loop.BreakStatement;
import galaga.gscript.ast.statement.logic.loop.ContinueStatement;
import galaga.gscript.ast.statement.logic.loop.DoWhileStatement;
import galaga.gscript.ast.statement.logic.loop.ForStatement;
import galaga.gscript.ast.statement.logic.loop.WhileStatement;
import galaga.gscript.lexer.rules.Keyword;
import galaga.gscript.lexer.rules.Operator;
import galaga.gscript.lexer.rules.OperatorPriority;
import galaga.gscript.lexer.token.Token;
import galaga.gscript.lexer.token.TokenException;
import galaga.gscript.lexer.token.TokenRange;
import galaga.gscript.lexer.token.TokenStream;
import galaga.gscript.lexer.token.TokenType;
import galaga.gscript.parser.Parser;

public class StatementParser extends SubParser {

    public StatementParser(Parser parser, TokenStream tokens) {
        super(parser, tokens);
    }

    public BlockStatement parseBlockStatement() {
        Token start = this.tokens.current();

        List<Statement> statements = new ArrayList<>();
        if (!this.tokens.match(Operator.LEFT_BRACE)) {
            this.parser.report(this.tokens.current(), "Expected '{' to begin block statement");
        }

        while (!this.tokens.check(Operator.RIGHT_BRACE, 0) && !this.tokens.isAtEnd()) {

            Statement stmt = null;
            try {
                stmt = this.parseStatement();
            } catch (TokenException e) {
                this.parser.report(tokens.current(), e.getMessage());
                this.tokens.advanceUntil(Operator.SEMICOLON, Operator.RIGHT_BRACE);
            }

            if (stmt != null) {
                statements.add(stmt);
            }
        }

        if (!this.tokens.match(Operator.RIGHT_BRACE)) {
            this.parser.report(this.tokens.current(), "Expected '{' to begin block statement");
        }
        this.tokens.match(Operator.SEMICOLON);

        Token end = this.tokens.previous();
        return new BlockStatement(statements, TokenRange.of(start, end));
    }

    private Statement parseStatement() throws TokenException {
        if (this.isIfStatement()) {
            return this.parseIfStatement();
        } else if (this.isWhileStatement()) {
            return this.parseWhileStatement();
        } else if (this.isDoWhileStatement()) {
            return this.parseDoWhileStatement();
        } else if (this.isForStatement()) {
            return this.parseForStatement();
        } else if (this.isReturnStatement()) {
            return this.parseReturnStatement();
        } else if (this.isBreakStatement()) {
            return this.parseBreakStatement();
        } else if (this.isContinueStatement()) {
            return this.parseContinueStatement();
        } else if (this.isAssignmentStatement()) {
            return this.parseAssignmentStatement();
        }
        return this.parseExpressionStatement();
    }

    private boolean isIfStatement() {
        return this.tokens.check(Keyword.IF, 0);
    }

    private boolean isWhileStatement() {
        return this.tokens.check(Keyword.WHILE, 0);
    }

    private boolean isDoWhileStatement() {
        return this.tokens.check(Keyword.DO, 0);
    }

    private boolean isForStatement() {
        return this.tokens.check(Keyword.FOR, 0);
    }

    private boolean isReturnStatement() {
        return this.tokens.check(Keyword.RETURN, 0);
    }

    private boolean isBreakStatement() {
        return this.tokens.check(Keyword.BREAK, 0);
    }

    private boolean isContinueStatement() {
        return this.tokens.check(Keyword.CONTINUE, 0);
    }

    private boolean isAssignmentStatement() throws TokenException {
        if (!this.tokens.check(TokenType.KEYWORD, 0)) {
            return false;
        }

        if (!this.tokens.check(TokenType.IDENTIFIER, 1)) {
            return false;
        }

        Token opToken = this.tokens.peek(2);
        if (!opToken.is(TokenType.OPERATOR)) {
            return false;
        }
        return OperatorPriority.isAssignmentOperator(opToken.getOperator());
    }

    private IfStatement parseIfStatement() throws TokenException {
        Token start = this.tokens.current();
        Map<Expression, BlockStatement> ifElseBranch = new LinkedHashMap<>();
        while (this.tokens.match(Keyword.IF) && !this.tokens.isAtEnd()) {
            if (ifElseBranch.size() > 0) {
                if (!this.tokens.match(Keyword.ELSE)) {
                    this.parser.report(this.tokens.current(), "Expected 'else' after 'if' branch");
                }
            }

            if (!this.tokens.match(Operator.LEFT_PAREN)) {
                this.parser.report(tokens.current(), "Expected '(' after 'if' or 'else if'");
            }

            Expression condition = null;
            try {
                condition = this.parser.getExpressionParser().parseExpression();
            } catch (TokenException e) {
                this.parser.report(tokens.current(), e.getMessage());
                this.tokens.advanceUntil(Operator.RIGHT_PAREN);
                return null;
            }

            if (!this.tokens.match(Operator.RIGHT_PAREN)) {
                this.parser.report(tokens.current(), "Expected ')' after if/else if condition");
            }

            BlockStatement body = this.parseBlockStatement();
            ifElseBranch.put(condition, body);
        }

        Optional<BlockStatement> elseBranch = Optional.empty();
        if (this.tokens.match(Keyword.ELSE)) {
            elseBranch = Optional.of(this.parseBlockStatement());
        }

        Token end = this.tokens.previous();
        return new IfStatement(ifElseBranch, elseBranch, TokenRange.of(start, end));
    }

    private WhileStatement parseWhileStatement() throws TokenException {
        Token start = this.tokens.current();

        this.tokens.consume(Keyword.WHILE, "Expected 'while' keyword");

        if (!this.tokens.match(Operator.LEFT_PAREN)) {
            this.parser.report(tokens.current(), "Expected '(' after 'while'");
        }

        Expression condition = null;
        try {
            condition = this.parser.getExpressionParser().parseExpression();
        } catch (TokenException e) {
            this.parser.report(tokens.current(), e.getMessage());
            this.tokens.advanceUntil(Operator.RIGHT_PAREN);
            return null;
        }

        if (!this.tokens.match(Operator.RIGHT_PAREN)) {
            this.parser.report(tokens.current(), "Expected ')' after while condition");
        }

        BlockStatement body = this.parseBlockStatement();

        Token end = this.tokens.previous();
        return new WhileStatement(condition, body, TokenRange.of(start, end));
    }

    private DoWhileStatement parseDoWhileStatement() throws TokenException {
        Token start = this.tokens.current();

        this.tokens.consume(Keyword.DO, "Expected 'do' keyword");

        BlockStatement body = this.parseBlockStatement();

        if (!this.tokens.match(Keyword.WHILE)) {
            this.parser.report(tokens.current(), "Expected 'while' after 'do' block");
        }

        if (!this.tokens.match(Operator.LEFT_PAREN)) {
            this.parser.report(tokens.current(), "Expected '(' after 'while'");
        }

        Expression condition = null;
        try {
            condition = this.parser.getExpressionParser().parseExpression();
        } catch (TokenException e) {
            this.parser.report(tokens.current(), e.getMessage());
            this.tokens.advanceUntil(Operator.RIGHT_PAREN);
            return null;
        }

        if (!this.tokens.match(Operator.RIGHT_PAREN)) {
            this.parser.report(tokens.current(), "Expected ')' after do-while condition");
        }
        this.tokens.match(Operator.SEMICOLON);

        Token end = this.tokens.previous();
        return new DoWhileStatement(condition, body, TokenRange.of(start, end));
    }

    private ForStatement parseForStatement() throws TokenException {
        Token start = this.tokens.current();

        this.tokens.consume(Keyword.FOR, "Expected 'for' keyword");

        if (!this.tokens.match(Operator.LEFT_PAREN)) {
            this.parser.report(tokens.current(), "Expected '(' after 'for'");
        }
        Token variable = this.tokens.consume(TokenType.IDENTIFIER, "Expected loop variable name");
        Expression iterable = null;
        try {
            iterable = this.parser.getExpressionParser().parseExpression();
        } catch (TokenException e) {
            this.parser.report(tokens.current(), e.getMessage());
            this.tokens.advanceUntil(Operator.RIGHT_PAREN);
            return null;
        }
        if (!this.tokens.match(Operator.RIGHT_PAREN)) {
            this.parser.report(tokens.current(), "Expected ')' after for loop iterable");
        }
        BlockStatement body = this.parseBlockStatement();

        Token end = this.tokens.previous();
        return new ForStatement(variable, iterable, body, TokenRange.of(start, end));
    }

    private ReturnStatement parseReturnStatement() throws TokenException {
        Token start = this.tokens.current();

        this.tokens.consume(Keyword.RETURN, "Expected 'return' keyword");

        Optional<Expression> value = Optional.empty();
        if (!this.tokens.check(Operator.SEMICOLON, 0)) {
            try {
                value = Optional.of(this.parser.getExpressionParser().parseExpression());
            } catch (TokenException e) {
                this.parser.report(tokens.current(), e.getMessage());
                this.tokens.advanceUntil(Operator.SEMICOLON);
                return null;
            }
        }
        this.tokens.match(Operator.SEMICOLON);

        Token end = this.tokens.previous();
        return new ReturnStatement(value, TokenRange.of(start, end));
    }

    private BreakStatement parseBreakStatement() throws TokenException {
        Token start = this.tokens.current();

        this.tokens.consume(Keyword.BREAK, "Expected 'break' keyword");
        this.tokens.match(Operator.SEMICOLON);

        Token end = this.tokens.previous();
        return new BreakStatement(TokenRange.of(start, end));
    }

    private ContinueStatement parseContinueStatement() throws TokenException {
        Token start = this.tokens.current();

        this.tokens.consume(Keyword.CONTINUE, "Expected 'continue' keyword");
        this.tokens.match(Operator.SEMICOLON);

        Token end = this.tokens.previous();
        return new ContinueStatement(TokenRange.of(start, end));
    }

    private AssignmentStatement parseAssignmentStatement() throws TokenException {
        Token start = this.tokens.current();

        Keyword vardecl = this.tokens.consume(TokenType.KEYWORD, "Expected 'const' or 'let' keyword").getKeyword();
        if (vardecl != Keyword.LET && vardecl != Keyword.CONST) {
            this.parser.report(tokens.current(), "Expected 'const' or 'let' keyword");
        }
        boolean isConstant = (vardecl == Keyword.CONST);
        Token name = this.tokens.consume(TokenType.IDENTIFIER, "Expected variable name");
        Token operator = this.tokens.consume(TokenType.OPERATOR, "Expected assignment operator");

        Expression value = null;
        try {
            value = this.parser.getExpressionParser().parseExpression();
        } catch (TokenException e) {
            this.parser.report(tokens.current(), e.getMessage());
            this.tokens.advanceUntil(Operator.SEMICOLON);
            return null;
        }
        this.tokens.match(Operator.SEMICOLON);

        Token end = this.tokens.previous();
        return new AssignmentStatement(name, operator, value, isConstant, TokenRange.of(start, end));
    }

    private ExpressionStatement parseExpressionStatement() throws TokenException {
        Token start = this.tokens.current();

        Expression expression = null;
        try {
            expression = this.parser.getExpressionParser().parseExpression();
        } catch (TokenException e) {
            this.parser.report(tokens.current(), e.getMessage());
            this.tokens.advanceUntil(Operator.SEMICOLON);
            return null;
        }
        this.tokens.match(Operator.SEMICOLON);

        Token end = this.tokens.previous();
        return new ExpressionStatement(expression, TokenRange.of(start, end));
    }
}
