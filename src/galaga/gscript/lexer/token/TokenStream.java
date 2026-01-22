package galaga.gscript.lexer.token;

import java.util.List;

import galaga.gscript.lexer.rules.Keyword;
import galaga.gscript.lexer.rules.Operator;

public final class TokenStream {
    private final List<Token> tokens;
    private int index = 0;

    public TokenStream(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Token peek() {
        return peek(0);
    }

    public Token peek(int offset) {
        int i = index + offset;
        if (i >= tokens.size()) {
            return tokens.get(tokens.size() - 1);
        }
        return tokens.get(i);
    }

    public Token previous() {
        return tokens.get(index - 1);
    }

    public boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }

    public Token advance() {
        if (!this.isAtEnd()) {
            index++;
        }
        return this.previous();
    }

    public void consume(TokenType type, String message) throws TokenException {
        if (this.check(type)) {
            this.advance();
            return;
        }
        throw new TokenException(this.peek(), message);
    }

    public void consume(Keyword keyword, String message) throws TokenException {
        if (this.check(keyword)) {
            this.advance();
            return;
        }
        throw new TokenException(this.peek(), message);
    }

    public void consume(Operator operator, String message) throws TokenException {
        if (this.check(operator)) {
            this.advance();
            return;
        }
        throw new TokenException(this.peek(), message);
    }

    public boolean check(TokenType type) {
        if (this.isAtEnd()) {
            return false;
        }
        return peek().getType() == type;
    }

    public boolean check(Keyword keyword) {
        if (this.isAtEnd()) {
            return false;
        }
        Token token = peek();
        return token.getType() == TokenType.KEYWORD && token.getValue().equals(keyword.getText());
    }

    public boolean check(Operator operator) {
        if (this.isAtEnd()) {
            return false;
        }
        Token token = peek();
        return token.getType() == TokenType.OPERATOR && token.getValue().equals(operator.getText());
    }

    public boolean match(TokenType type) {
        if (this.check(type)) {
            this.advance();
            return true;
        }
        return false;
    }

    public boolean match(Keyword keyword) {
        if (this.check(keyword)) {
            this.advance();
            return true;
        }
        return false;
    }

    public boolean match(Operator operator) {
        if (this.check(operator)) {
            this.advance();
            return true;
        }
        return false;
    }
}
