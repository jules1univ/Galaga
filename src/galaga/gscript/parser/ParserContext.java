package galaga.gscript.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import galaga.gscript.ast.Program;
import galaga.gscript.ast.declaration.Declaration;
import galaga.gscript.ast.declaration.ErrorDeclaration;
import galaga.gscript.lexer.Lexer;
import galaga.gscript.lexer.rules.Keyword;
import galaga.gscript.lexer.rules.Operator;
import galaga.gscript.lexer.token.Token;
import galaga.gscript.lexer.token.TokenType;

public final class ParserContext {
    private final List<Declaration> declarations = new ArrayList<>();
    private final List<Token> tokens;

    private int index = 0;
    private Token current = null;

    public static ParserContext of(Lexer lexer) {
        List<Token> tokens = new ArrayList<>();
        lexer.iterator().forEachRemaining(tokens::add);
        return new ParserContext(tokens);
    }

    public static ParserContext of(List<Token> tokens) {
        return new ParserContext(tokens);
    }

    public ParserContext(List<Token> tokens) {
        this.tokens = tokens.stream()
                .filter(token -> token.getType() != TokenType.COMMENT)
                .collect(Collectors.toList());
        this.current = this.tokens.get(0);
    }

    public boolean isEnd() {
        return this.index >= this.tokens.size();
    }

    public void advance() {
        this.index++;
        if (!this.isEnd()) {
            this.current = this.tokens.get(this.index);
        }
    }

    public boolean expect(TokenType type) {
        if (this.current.getType() != type) {
            this.pushError("Unexpected token: expected %s but got %s", type, this.current.getType());
            return false;
        }
        this.advance();
        return true;
    }

    public boolean expect(TokenType type, String value) {
        if (this.current.getType() != type || !this.current.getValue().equals(value)) {
            this.pushError("Unexpected token: expected %s('%s') but got %s('%s')", type, value, this.current.getType(),
                    this.current.getValue());
            return false;
        }
        this.advance();
        return true;
    }

    public boolean expect(Keyword keyword) {
        return this.expect(TokenType.KEYWORD, keyword.getText());
    }

    public boolean expect(Operator operator) {
        return this.expect(TokenType.OPERATOR, operator.getText());
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


    public Optional<String> getValueExpect(TokenType type) {
        if (!this.expect(type)) {
            return Optional.empty();
        }
        return Optional.of(this.tokens.get(this.index - 1).getValue());
    }

    public Optional<String> getValueExpect(Keyword keyword) {
        if (!this.expect(keyword)) {
            return Optional.empty();
        }
        return Optional.of(this.tokens.get(this.index - 1).getValue());
    }

    public Optional<String> getValueExpect(Operator operator) {
        if (!this.expect(operator)) {
            return Optional.empty();
        }
        return Optional.of(this.tokens.get(this.index - 1).getValue());
    }

    public void pushError(String message, Object... args) {
        this.declarations.add(new ErrorDeclaration(String.format(message, args)));
    }

    public void push(Declaration declaration) {
        this.declarations.add(declaration);
    }

    public Program build() {
        return new Program(this.declarations);
    }
}
