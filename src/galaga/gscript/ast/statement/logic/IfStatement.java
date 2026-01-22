package galaga.gscript.ast.statement.logic;

import java.util.Optional;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.expression.Expression;
import galaga.gscript.ast.statement.BlockStatement;
import galaga.gscript.ast.statement.Statement;

public record IfStatement(Expression condition, BlockStatement thenBranch, Optional<BlockStatement> elseBranch) implements Statement {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitIfStatement(this);
    }
}