package galaga.gscript.ast.expression.collection;

import java.util.Map;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.expression.Expression;

public record MapExpression(Map<Expression, Expression> entries) implements Expression {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitMapExpression(this);
    }

}