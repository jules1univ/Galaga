package galaga.gscript.ast.expression.collection;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.expression.Expression;

public record RangeExpression(Expression start, Expression end, boolean inclusive) implements Expression {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitRangeExpression(this);
    }
}