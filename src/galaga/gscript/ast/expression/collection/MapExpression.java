package galaga.gscript.ast.expression.collection;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.expression.Expression;

public record MapExpression(Expression key, Expression value) implements Expression {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitMapExpression(this);
    }

}