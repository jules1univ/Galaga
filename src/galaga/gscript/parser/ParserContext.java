package galaga.gscript.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import galaga.gscript.lexer.Lexer;
import galaga.gscript.lexer.rules.Keyword;
import galaga.gscript.lexer.rules.Operator;
import galaga.gscript.lexer.token.Token;
import galaga.gscript.lexer.token.TokenType;

public final class ParserContext {
    private final List<Token> tokens;
    private final String[] lines;

    private int index = 0;
    private Token current = null;

    public static ParserContext of(Lexer lexer) {
        List<Token> tokens = new ArrayList<>();
        lexer.iterator().forEachRemaining(tokens::add);
        return new ParserContext(lexer.getSource(), tokens);
    }

    public static ParserContext of(String source, List<Token> tokens) {
        return new ParserContext(source, tokens);
    }

    private ParserContext(String source, List<Token> tokens) {
        this.lines = source.split("\n");
        this.tokens = tokens.stream()
                .filter(token -> token.getType() != TokenType.COMMENT)
                .collect(Collectors.toList());
        this.current = this.tokens.get(0);
    }

    public int getIndex() {
        return this.index;
    }

    public Token getCurrentToken() {
        return this.current;
    }

    public boolean isEnd() {
        return this.index >= this.tokens.size() || this.current.getType() == TokenType.EOF;
    }

    public void advance() {
        this.index++;
        if (!this.isEnd()) {
            this.current = this.tokens.get(this.index);
        }
    }

    public void expect(TokenType type) throws ParserException {
        if (this.current.getType() != type) {
            throw new ParserException(this, "Unexpected token: expected %s but got %s", type, this.current.getType());
        }

        this.advance();
    }

    public void expect(TokenType type, String value) throws ParserException {
        if (this.current.getType() != type || !this.current.getValue().equals(value)) {
            throw new ParserException(this, "Unexpected token: expected %s('%s') but got %s('%s')", type, value,
                    this.current.getType(), this.current.getValue());
        }
        this.advance();
    }

    public void expect(Keyword keyword) throws ParserException {
        this.expect(TokenType.KEYWORD, keyword.getText());
    }

    public void expect(Operator operator) throws ParserException {
        this.expect(TokenType.OPERATOR, operator.getText());
    }

    public boolean is(TokenType type, String value) {
        return this.current.getType() == type && this.current.getValue().equals(value);
    }

    public boolean is(TokenType type) {
        return this.current.getType() == type;
    }

    public boolean is(Keyword keyword) {
        return this.is(TokenType.KEYWORD, keyword.getText());
    }

    public boolean is(Operator operator) {
        return this.is(TokenType.OPERATOR, operator.getText());
    }

    public boolean isAndAdvance(TokenType type) {
        if (this.is(type)) {
            this.advance();
            return true;
        }
        return false;
    }

    public boolean isAndAdvance(Keyword keyword) {
        if (this.is(keyword)) {
            this.advance();
            return true;
        }
        return false;
    }

    public boolean isAndAdvance(Operator operator) {
        if (this.is(operator)) {
            this.advance();
            return true;
        }
        return false;
    }

    public boolean nextIs(TokenType type) {
        if (this.index + 1 >= this.tokens.size()) {
            return false;
        }
        Token next = this.tokens.get(this.index + 1);
        return next.getType() == type;
    }

    public boolean nextIs(Operator operator) {
        if (this.index + 1 >= this.tokens.size()) {
            return false;
        }
        Token next = this.tokens.get(this.index + 1);
        return next.getType() == TokenType.OPERATOR && next.getValue().equals(operator.getText());
    }

    public boolean nextIs(Keyword keyword) {
        if (this.index + 1 >= this.tokens.size()) {
            return false;
        }
        Token next = this.tokens.get(this.index + 1);
        return next.getType() == TokenType.KEYWORD && next.getValue().equals(keyword.getText());
    }

    public void advanceIfSemicolon() {
        if (this.is(Operator.SEMICOLON)) {
            this.advance();
        }
    }

    public String getValue() {
        return this.current.getValue();
    }

    public String getValueAndAdvance() {
        String value = this.current.getValue();
        this.advance();
        return value;
    }

    public Operator getOperator() {
        String value = this.current.getValue();
        return Operator.fromText(value);
    }

    public Keyword getKeyword() {
        String value = this.current.getValue();
        return Keyword.fromText(value);
    }

    public Optional<String> getValueIf(TokenType type) {
        if (this.is(type)) {
            String value = this.current.getValue();
            this.advance();
            return Optional.of(value);
        }
        return Optional.empty();
    }

    public Optional<String> getValueIf(Keyword keyword) {
        if (this.is(keyword)) {
            String value = this.current.getValue();
            this.advance();
            return Optional.of(value);
        }
        return Optional.empty();
    }

    public Optional<String> getValueIf(Operator operator) {
        if (this.is(operator)) {
            String value = this.current.getValue();
            this.advance();
            return Optional.of(value);
        }
        return Optional.empty();
    }

    public String getValueExpect(TokenType type) throws ParserException {
        String value = this.current.getValue();
        this.expect(type);
        return value;
    }

    public String getErrorContext(String message) {
        StringBuilder sb = new StringBuilder();
        int startLine = this.current.getStart().getLine();

        int contextStart = Math.max(0, startLine - 4);
        int contextEnd = Math.min(this.lines.length, startLine + 4);

        sb.append("\n\n");
        for (int i = contextStart; i < contextEnd; i++) {
            sb.append(String.format("%4d %s\n", i + 1, this.lines[i]));
            if (i + 1 == startLine) {
                for (int j = 0; j < this.current.getStart().getColumn() + 3; j++) {
                    sb.append(" ");
                }
                for (int j = 0; j < this.current.getValue().length(); j++) {
                    sb.append("^");
                }
                sb.append(String.format(" %s\n", message));
            }
        }
        sb.append("\n\n");
        return sb.toString();

    }
}
