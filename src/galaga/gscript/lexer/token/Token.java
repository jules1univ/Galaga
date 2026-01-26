package galaga.gscript.lexer.token;

import galaga.gscript.lexer.rules.Keyword;
import galaga.gscript.lexer.rules.Operator;

public final class Token {
    private final TokenPosition start;
    private final TokenPosition end;

    private final TokenType type;
    private final String value;

    public static Token of(TokenType type, TokenPosition start, String value) {
        return new Token(type, start, TokenPosition.of(start.getLine(), start.getColumn() + value.length()), value);
    }

    private Token(TokenType type, TokenPosition start, TokenPosition end, String value) {
        this.type = type;
        this.start = start;
        this.end = end;
        this.value = value;
    }

    public TokenPosition getStart() {
        return this.start;
    }

    public TokenPosition getEnd() {
        return this.end;
    }

    public TokenType getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }

    public Operator getOperator() throws TokenException {
        if (this.type != TokenType.OPERATOR) {
            throw new TokenException(this, "Token is not an operator");
        }
        return Operator.fromText(this.value);
    }

    public Keyword getKeyword() throws TokenException {
        if (this.type != TokenType.KEYWORD) {
            throw new TokenException(this, "Token is not a keyword");
        }
        return Keyword.fromText(this.value);
    }

    public boolean is(TokenType type) {
        return this.type == type;
    }

    public boolean is(Keyword keyword) {
        return Keyword.isKeyword(this.value) && this.value.equals(keyword.getText());
    }

    public boolean is(Operator operator) {
        return Operator.isOperator(this.value) && this.value.equals(operator.getText());
    }

    @Override
    public String toString() {
        return type + "('" + value + "')";
    }
}
