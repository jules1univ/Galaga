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

    public int begin() {
        return index;
    }

    public List<Token> end(int begin) {
        return tokens.subList(begin, index);
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

    public Token consume(TokenType type, String message) throws TokenException {
        if (this.check(type, 0)) {
            this.advance();
            return this.previous();
        }
        throw new TokenException(this.peek(), message);
    }

    public Token consume(Keyword keyword, String message) throws TokenException {
        if (this.check(keyword, 0)) {
            this.advance();
            return this.previous();
        }
        throw new TokenException(this.peek(), message);
    }

    public Token consume(Operator operator, String message) throws TokenException {
        if (this.check(operator, 0)) {
            this.advance();
            return this.previous();
        }
        throw new TokenException(this.peek(), message);
    }

    public boolean check(TokenType type, int index) {
        if (this.isAtEnd() || index >= tokens.size()) {
            return false;
        }
        return peek(index).getType() == type;
    }

    public boolean check(Keyword keyword, int index) {
        if (this.isAtEnd() || index >= tokens.size()) {
            return false;
        }
        Token token = peek(index);
        return token.getType() == TokenType.KEYWORD && token.getValue().equals(keyword.getText());
    }

    public boolean check(Operator operator, int index) {
        if (this.isAtEnd() || index >= tokens.size()) {
            return false;
        }
        Token token = peek(index);
        return token.getType() == TokenType.OPERATOR && token.getValue().equals(operator.getText());
    }

    public boolean match(TokenType type) {
        if (this.check(type, 0)) {
            this.advance();
            return true;
        }
        return false;
    }

    public boolean match(Keyword keyword) {
        if (this.check(keyword, 0)) {
            this.advance();
            return true;
        }
        return false;
    }

    public boolean match(Operator operator) {
        if (this.check(operator, 0)) {
            this.advance();
            return true;
        }
        return false;
    }
}
