package galaga.gscript.lexer;

import java.util.ArrayList;
import java.util.Iterator;

import galaga.gscript.lexer.token.Token;
import galaga.gscript.lexer.token.TokenPosition;
import galaga.gscript.lexer.token.TokenType;

public final class Lexer implements Iterable<Token> {
    private final String source;
    private final ArrayList<Token> tokens = new ArrayList<>();

    private char current = '\0';
    private int index = 0;
    private int line = 1;
    private int column = 1;

    public static Lexer of(String source) {
        return new Lexer(source);
    }

    private Lexer(String source) {
        this.source = source;
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

    private void identifier() {
        TokenPosition position = TokenPosition.of(this.line, this.column);
        String value = "";
        while (Character.isLetterOrDigit(current) || current == '_') {
            value += current;
            advance();
        }

        if (Keyword.isKeyword(value)) {
            this.tokens.add(Token.of(TokenType.KEYWORD, position, value));
            return;
        }
        this.tokens.add(Token.of(TokenType.IDENTIFIER, position, value));
    }

    private void numberLiteral() {
        TokenPosition position = TokenPosition.of(this.line, this.column);
        String value = "";
        while (Character.isDigit(current)) {
            value += current;
            advance();
        }
        this.tokens.add(Token.of(TokenType.NUMBER, position, value));
    }

    private void stringLiteral() {
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
        this.tokens.add(Token.of(TokenType.STRING, position, value));
    }

    private void operator() throws LexerError {
        TokenPosition position = TokenPosition.of(this.line, this.column);
        String value = "";

        while (!Character.isLetterOrDigit(current) && !Character.isWhitespace(current) && current != '\0') {
            value += current;
            advance();
            break;
        }

        if (Operator.isOperator(value)) {
            this.tokens.add(Token.of(TokenType.OPERATOR, position, value));
            return;
        }

        throw new LexerError(position, value, "Unknown operator");
    }

    private void comment() {
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
            this.tokens.add(Token.of(TokenType.COMMENT, position, value));
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
            this.tokens.add(Token.of(TokenType.COMMENT, position, value));
        }
    }

    public void execute() throws LexerError {
        if (this.source.isEmpty()) {
            return;
        }
        this.tokens.clear();

        this.advance();
        while (!this.isEnd()) {
            if (Character.isWhitespace(current)) {
                this.whitespace();
            } else if (Character.isLetter(current) || current == '_') {
                this.identifier();
            } else if (Character.isDigit(current)) {
                this.numberLiteral();
            } else if (current == '"' || current == '\'') {
                this.stringLiteral();
            } else if (current == '/' && (this.peek() == '/' || this.peek() == '*')) {
                this.comment();
            } else {
                this.operator();
            }
        }
        this.tokens.add(Token.of(TokenType.EOF, TokenPosition.of(this.line, this.column), ""));
    }

    @Override
    public Iterator<Token> iterator() {
        return tokens.iterator();
    }

}
