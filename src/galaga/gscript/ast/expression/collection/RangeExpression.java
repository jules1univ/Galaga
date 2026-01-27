package galaga.gscript.ast.expression.collection;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.expression.Expression;
import galaga.gscript.lexer.token.TokenRange;

public record RangeExpression(Expression start, Expression end, TokenRange range) implements Expression {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitRangeExpression(this);
    }

    
    @Override
    public TokenRange getRange() {
        return range;
    }
}