package galaga.gscript.parser;

import galaga.gscript.ast.Program;
import galaga.gscript.ast.declaration.Declaration;
import galaga.gscript.lexer.Lexer;
import galaga.gscript.lexer.token.Token;
import galaga.gscript.lexer.token.TokenException;
import galaga.gscript.lexer.token.TokenRange;
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

    public Parser(Lexer lexer) {
        this(lexer.tokenize());
    }

    public Parser(TokenStream tokens) {
        this.tokens = tokens;
        this.errors = new ArrayList<>();

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
        return errors;    
    }


    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void report(Token token, String message) {
        errors.add(new ParseError(token, message));
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
                this.report(tokens.current(), "Unexpected error: " + e.getMessage());
                this.tokens.advanceUntil(decl.getDeclarationStarters());
            }
        }

        return new Program(declarations, TokenRange.of(this.tokens.getFirst(), this.tokens.getLast()));
    }

    private Declaration parseTopLevelDeclaration() throws TokenException {
        if (decl.isFunctionDeclaration()) {
            return decl.parseFunctionDeclaration();
        } else if (decl.isNativeDeclaration()) {
            return decl.parseNativeFunctionDeclaration();
        } else if (decl.isAtVariableDeclaration()) {
            return decl.parseVariableDeclaration();
        } else {
            report(tokens.current(),
                    "Expected declaration, found: " + tokens.current());
            this.tokens.advanceUntil(decl.getDeclarationStarters());
            return null;
        }
    }
}