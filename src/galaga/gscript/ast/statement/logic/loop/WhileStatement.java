package galaga.gscript.ast.statement.logic.loop;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.expression.Expression;
import galaga.gscript.ast.statement.BlockStatement;
import galaga.gscript.ast.statement.Statement;
import galaga.gscript.lexer.token.TokenRange;

public record WhileStatement(Expression condition, BlockStatement body, TokenRange range) implements Statement {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitWhileStatement(this);
    }

    
    @Override
    public TokenRange getRange() {
        return range;
    }
}