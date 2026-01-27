package galaga.gscript.ast.expression.collection;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.expression.Expression;
import galaga.gscript.lexer.token.TokenRange;

public record IndexExpression(Expression target, Expression index, TokenRange range) implements Expression {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitIndexExpression(this);
    }

    
    @Override
    public TokenRange getRange() {
        return range;
    }
}