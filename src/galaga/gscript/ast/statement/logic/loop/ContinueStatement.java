package galaga.gscript.ast.statement.logic.loop;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.statement.Statement;

public record ContinueStatement() implements Statement {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitContinueStatement(this);
    }
}