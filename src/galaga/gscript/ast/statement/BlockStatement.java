package galaga.gscript.ast.statement;

import java.util.List;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.lexer.token.TokenRange;

public record BlockStatement(List<Statement> statements, TokenRange range) implements Statement {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitBlockStatement(this);
    }

    
    @Override
    public TokenRange getRange() {
        return range;
    }
}