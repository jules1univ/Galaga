package galaga.gscript.ast.statement.logic.loop;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.statement.Statement;

public record BreakStatement() implements Statement {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitBreakStatement(this);
    }
}