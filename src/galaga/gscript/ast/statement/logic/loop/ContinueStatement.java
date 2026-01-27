package galaga.gscript.ast.statement.logic.loop;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.statement.Statement;
import galaga.gscript.lexer.token.TokenRange;

public record ContinueStatement(TokenRange range) implements Statement {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitContinueStatement(this);
    }

    
    @Override
    public TokenRange getRange() {
        return range;
    }
}