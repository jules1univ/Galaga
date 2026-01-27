package galaga.gscript.ast.statement;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.expression.Expression;
import galaga.gscript.lexer.token.TokenRange;

public record ExpressionStatement(Expression expression, TokenRange range) implements Statement {
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitExpressionStatement(this);
    }

    
    @Override
    public TokenRange getRange() {
        return range;
    }
}