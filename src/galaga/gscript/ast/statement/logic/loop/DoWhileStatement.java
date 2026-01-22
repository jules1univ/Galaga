package galaga.gscript.ast.statement.logic.loop;


import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.expression.Expression;
import galaga.gscript.ast.statement.BlockStatement;
import galaga.gscript.ast.statement.Statement;

public record DoWhileStatement(Expression condition, BlockStatement body) implements Statement {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitDoWhileStatement(this);
    }
}