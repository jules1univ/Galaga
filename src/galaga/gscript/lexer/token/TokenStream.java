package galaga.gscript.lexer.token;

import java.util.List;
import java.util.function.Function;

import galaga.gscript.lexer.rules.Keyword;
import galaga.gscript.lexer.rules.Operator;

public final class TokenStream {
    private final List<Token> tokens;
    private int index = 0;

    public TokenStream(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Token getFirst() {
        return tokens.get(0);
    }

    public Token getLast() {
        return tokens.get(tokens.size() - 1);
    }

    public Token current() {
        return peek(0);
    }

    public Token peek() {
        return peek(0);
    }

    public Token peek(int offset) {
        int i = index + offset;
        if (i < 0) {
            return tokens.get(0);
        }
        if (i >= tokens.size()) {
            return tokens.get(tokens.size() - 1);
        }
        return tokens.get(i);
    }

    public Token previous() {
        return previous(1);
    }

    public Token previous(int offset) {
        int i = index - offset;
        if (i < 0) {
            return tokens.get(0);
        }
        if (i >= tokens.size()) {
            return tokens.get(tokens.size() - 1);
        }
        return tokens.get(i);
    }

    public boolean isAtEnd() {
        return current().getType() == TokenType.EOF;
    }

    public Token advance() {
        if (!isAtEnd()) {
            index++;
        }
        return previous();
    }

    public Token rewind() {
        if (index > 0) {
            index--;
        }
        return current();
    }

    public void skip(int count) {
        for (int i = 0; i < count && !isAtEnd(); i++) {
            advance();
        }
    }

    public void seek(int position) {
        if (position >= 0 && position < tokens.size()) {
            index = position;
        }
    }

    public boolean check(TokenType type, int offset) {
        Token token = peek(offset);
        return token.is(type);
    }

    public boolean check(TokenType type) {
        return check(type, 0);
    }

    public boolean check(Keyword keyword, int offset) {
        Token token = peek(offset);
        return token.is(keyword);
    }

    public boolean check(Keyword keyword) {
        return check(keyword, 0);
    }

    public boolean check(Operator operator, int offset) {
        Token token = peek(offset);
        return token.is(operator);
    }

    public boolean check(Operator operator) {
        return check(operator, 0);
    }

    public boolean check(TokenType... types) {
        for (int i = 0; i < types.length; i++) {
            if (!check(types[i], i)) {
                return false;
            }
        }
        return true;
    }

    public boolean match(TokenType type) {
        if (check(type, 0)) {
            advance();
            return true;
        }
        return false;
    }

    public boolean match(Keyword keyword) {
        if (check(keyword, 0)) {
            advance();
            return true;
        }
        return false;
    }

    public boolean match(Operator operator) {
        if (check(operator, 0)) {
            advance();
            return true;
        }
        return false;
    }

    public boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (match(type)) {
                return true;
            }
        }
        return false;
    }

    public boolean match(Keyword... keywords) {
        for (Keyword keyword : keywords) {
            if (match(keyword)) {
                return true;
            }
        }
        return false;
    }

    public boolean match(Operator... operators) {
        for (Operator operator : operators) {
            if (match(operator)) {
                return true;
            }
        }
        return false;
    }

    public Token tryConsume(TokenType type) {
        if (check(type, 0)) {
            return advance();
        }
        return null;
    }

    public Token tryConsume(Keyword keyword) {
        if (check(keyword, 0)) {
            return advance();
        }
        return null;
    }

    public Token tryConsume(Operator operator) {
        if (check(operator, 0)) {
            return advance();
        }
        return null;
    }

    public Token consume(TokenType type, String message) throws TokenException {
        if (check(type, 0)) {
            return advance();
        }
        throw new TokenException(current(), message);
    }

    public Token consume(Keyword keyword, String message) throws TokenException {
        if (check(keyword, 0)) {
            return advance();
        }
        throw new TokenException(current(), message);
    }

    public Token consume(Operator operator, String message) throws TokenException {
        if (check(operator, 0)) {
            return advance();
        }
        throw new TokenException(current(), message);
    }

    public Token expect(TokenType type, String errorMessage, Function<String, Void> errorReporter) {
        if (check(type, 0)) {
            return advance();
        }

        errorReporter.apply(errorMessage);
        return null;
    }

    public Token expect(Keyword type, String errorMessage, Function<String, Void> errorReporter) {
        if (check(type, 0)) {
            return advance();
        }

        errorReporter.apply(errorMessage);
        return null;
    }

    public Token expect(Operator type, String errorMessage, Function<String, Void> errorReporter) {
        if (check(type, 0)) {
            return advance();
        }

        errorReporter.apply(errorMessage);
        return null;
    }

    public boolean advanceUntil(TokenType... type) {
        while (!isAtEnd()) {
            for (TokenType t : type) {
                if (check(t, 0)) {
                    return true;
                }
            }
            advance();
        }
        return false;
    }

    public boolean advanceUntil(Keyword... keyword) {
        while (!isAtEnd()) {
            for (Keyword k : keyword) {
                if (check(k, 0)) {
                    return true;
                }
            }
            advance();
        }
        return false;
    }

    public boolean advanceUntil(Operator... operator) {
        while (!isAtEnd()) {
            for (Operator o : operator) {
                if (check(o, 0)) {
                    return true;
                }
            }
            advance();
        }
        return false;
    }

    public String getContext(int radius) {
        StringBuilder sb = new StringBuilder();
        int start = Math.max(0, index - radius);
        int end = Math.min(tokens.size(), index + radius + 1);

        for (int i = start; i < end; i++) {
            if (i == index) {
                sb.append(" >>> ");
            }
            sb.append(tokens.get(i));
            if (i == index) {
                sb.append(" <<< ");
            }
            if (i < end - 1) {
                sb.append(" ");
            }
        }

        return sb.toString();
    }
}