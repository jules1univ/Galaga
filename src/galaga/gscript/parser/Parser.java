package galaga.gscript.parser;

import galaga.gscript.ast.Program;
import galaga.gscript.ast.declaration.Declaration;
import galaga.gscript.lexer.Lexer;
import galaga.gscript.lexer.token.Token;
import galaga.gscript.lexer.token.TokenException;
import galaga.gscript.lexer.token.TokenStream;
import galaga.gscript.parser.subparser.DeclarationParser;
import galaga.gscript.parser.subparser.ExpressionParser;
import galaga.gscript.parser.subparser.StatementParser;

import java.util.ArrayList;
import java.util.List;

public final class Parser {
    private final TokenStream tokens;
    private final DeclarationParser decl;
    private final StatementParser stmt;
    private final ExpressionParser expr;

    private final List<ParseError> errors;
    private boolean panicMode;

    public Parser(Lexer lexer) {
        this(lexer.tokenize());
    }

    public Parser(TokenStream tokens) {
        this.tokens = tokens;
        this.errors = new ArrayList<>();
        this.panicMode = false;

        this.decl = new DeclarationParser(this, this.tokens);
        this.stmt = new StatementParser(this, this.tokens);
        this.expr = new ExpressionParser(this, this.tokens);
    }

    public ExpressionParser getExpressionParser() {
        return this.expr;
    }

    public DeclarationParser getDeclarationParser() {
        return this.decl;
    }

    public StatementParser getStatementParser() {
        return this.stmt;
    }

    public List<ParseError> getErrors() {
        return new ArrayList<>(errors);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean hasFatalErrors() {
        return errors.stream().anyMatch(e -> e.level() == ParseErrorLevel.FATAL);
    }

    public void report(Token token, String message, ParseErrorLevel level) {
        if (panicMode && level != ParseErrorLevel.FATAL) {
            return;
        }

        errors.add(new ParseError(token, message, level));
        if (level == ParseErrorLevel.FATAL) {
            panicMode = true;
        }
    }

    public void reportError(Token token, String message) {
        this.report(token, message, ParseErrorLevel.ERROR);
    }

    public void reportWarning(Token token, String message) {
        this.report(token, message, ParseErrorLevel.WARNING);
    }

    public Program parse() {
        List<Declaration> declarations = new ArrayList<>();

        while (!tokens.isAtEnd()) {
            try {
                Declaration declaration = parseTopLevelDeclaration();
                if (declaration != null) {
                    declarations.add(declaration);
                }
            } catch (Exception e) {
                this.reportError(tokens.current(), "Unexpected error: " + e.getMessage());
                this.tokens.advanceUntil(decl.getDeclarationStarters());
            }
        }

        return new Program(declarations);
    }

    private Declaration parseTopLevelDeclaration() throws TokenException {
        if (decl.isFunctionDeclaration()) {
            return decl.parseFunctionDeclaration();
        } else if (decl.isNativeDeclaration()) {
            return decl.parseNativeFunctionDeclaration();
        } else if (decl.isAtVariableDeclaration()) {
            return decl.parseVariableDeclaration();
        } else {
            reportError(tokens.current(),
                    "Expected declaration, found: " + tokens.current());
            this.tokens.advanceUntil(decl.getDeclarationStarters());
            return null;
        }
    }
}