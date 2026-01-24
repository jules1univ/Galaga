package galaga.gscript.parser;

import galaga.gscript.ast.Program;
import galaga.gscript.lexer.Lexer;
import galaga.gscript.lexer.token.TokenStream;
import galaga.gscript.parser.subparser.DeclarationParser;
import galaga.gscript.parser.subparser.ExpressionParser;
import galaga.gscript.parser.subparser.StatementParser;

public final class Parser {
    private final TokenStream tokens;
    private final DeclarationParser decl;
    private final StatementParser stmt;
    private final ExpressionParser expr;

    public Parser(Lexer lexer) {
        this.tokens = lexer.tokenize();
        this.decl = new DeclarationParser(this, this.tokens);
        this.stmt = new StatementParser(this.tokens);
        this.expr = new ExpressionParser(this.tokens);
    }

    public Parser(TokenStream tokens) {
        this.tokens = tokens;
        this.decl = new DeclarationParser(this, this.tokens);
        this.stmt = new StatementParser(this.tokens);
        this.expr = new ExpressionParser(this.tokens);
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

    public Program parse() {
        return new Program();
    }
}
