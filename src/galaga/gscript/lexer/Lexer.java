package galaga.gscript.lexer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import galaga.gscript.lexer.rules.Keyword;
import galaga.gscript.lexer.rules.Operator;
import galaga.gscript.lexer.token.Token;
import galaga.gscript.lexer.token.TokenPosition;
import galaga.gscript.lexer.token.TokenType;

public final class Lexer implements Iterable<Token> {
    private static final char EOF = '\0';
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private char current = EOF;
    private int index = 0;
    private int line = 1;
    private int column = 1;

    public static Lexer of(String source) {
        return new Lexer(source);
    }

    private Lexer(String source) {
        this.source = source;
    }

    public String getSource() {
        return this.source;
    }

    private boolean isEnd() {
        return this.index >= this.source.length();
    }

    private void advance() {
        if (!this.isEnd()) {
            this.current = this.source.charAt(this.index);
            this.index++;
            this.column++;
            if (this.current == '\n') {
                this.line++;
                this.column = 1;
            }
        } else {
            this.current = '\0';
        }
    }

    private char peek() {
        if (this.index >= this.source.length()) {
            return '\0';
        }
        return this.source.charAt(this.index);
    }

    private void whitespace() {
        while (Character.isWhitespace(current)) {
            advance();
        }
    }

    private Token identifier() {
        TokenPosition position = TokenPosition.of(this.line, this.column);
        String value = "";
        while (Character.isLetterOrDigit(current) || current == '_') {
            value += current;
            advance();
        }

        if (Keyword.isKeyword(value)) {
            return Token.of(TokenType.KEYWORD, position, value);
        }
        return Token.of(TokenType.IDENTIFIER, position, value);
    }

    private Token numberLiteral() {
        TokenPosition position = TokenPosition.of(this.line, this.column);
        String value = "";
        while (Character.isDigit(current)) {
            value += current;
            advance();
        }
        return Token.of(TokenType.NUMBER, position, value);
    }

    private Token stringLiteral() {
        TokenPosition position = TokenPosition.of(this.line, this.column);
        String value = "";

        char quoteType = current;
        this.advance();
        while (current != quoteType && !isEnd()) {
            if (current == '\\') {
                this.advance();
                switch (current) {
                    case 'n' -> value += '\n';
                    case 't' -> value += '\t';
                    case 'r' -> value += '\r';
                    case '"' -> value += '"';
                    case '\\' -> value += '\\';
                    default -> value += current;
                }
                this.advance();
                continue;
            }
            value += current;
            this.advance();
        }
        this.advance();
        return Token.of(TokenType.STRING, position, value);
    }

    private Token operator() {
        TokenPosition position = TokenPosition.of(this.line, this.column);

        char first = current;
        char second = peek();

        if (second != '\0') {
            String two = "" + first + second;
            if (Operator.isOperator(two)) {
                advance();
                advance();
                return Token.of(TokenType.OPERATOR, position, two);
            }
        }

        String one = String.valueOf(first);
        if (Operator.isOperator(one)) {
            advance();
            return Token.of(TokenType.OPERATOR, position, one);
        }

        advance();
        return Token.of(TokenType.UNKNOWN, position, one);
    }

    private Token comment() {
        TokenPosition position = TokenPosition.of(this.line, this.column);
        String value = "";

        if (current == '/' && this.peek() == '/') {
            value += current;
            advance();
            value += current;
            advance();
            while (current != '\n' && !this.isEnd()) {
                value += current;
                advance();
            }
            return Token.of(TokenType.COMMENT, position, value);
        } else if (current == '/' && this.peek() == '*') {
            value += current;
            advance();
            value += current;
            advance();
            while (!(current == '*' && this.peek() == '/') && !this.isEnd()) {
                value += current;
                advance();
            }
            value += current;
            advance();
            value += current;
            advance();

            return Token.of(TokenType.COMMENT, position, value);
        }

        return Token.of(TokenType.UNKNOWN, position, value);
    }

    public List<Token> getTokens() {
        return this.tokens;
    }

    @Override
    public Iterator<Token> iterator() {
        this.advance();
        return new Iterator<Token>() {
            private boolean hasEOF = false;

            @Override
            public boolean hasNext() {
                return !this.hasEOF;
            }

            @Override
            public Token next() {
                if (Character.isWhitespace(current)) {
                    whitespace();
                }

                if (current == EOF) {
                    this.hasEOF = true;
                    return Token.of(TokenType.EOF, TokenPosition.of(line, column), "");
                }

                Token token;
                if (current == '/' && (peek() == '/' || peek() == '*')) {
                    token = comment();
                } else if (Character.isLetter(current) || current == '_') {
                    token = identifier();
                } else if (Character.isDigit(current)) {
                    token = numberLiteral();
                } else if (current == '"' || current == '\'') {
                    token = stringLiteral();
                } else {
                    token = operator();
                }

                tokens.add(token);
                return token;
            }
        };
    }

}
