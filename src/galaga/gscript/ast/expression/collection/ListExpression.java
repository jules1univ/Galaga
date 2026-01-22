package galaga.gscript.ast.expression.collection;

import java.util.List;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.expression.Expression;

public record ListExpression(List<Expression> elements) implements Expression {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitListExpression(this);
    }
}