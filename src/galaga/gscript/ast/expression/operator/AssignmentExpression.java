package galaga.gscript.ast.expression.operator;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.expression.Expression;

public record AssignmentExpression(Expression target, Expression value) implements Expression {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitAssignmentExpression(this);
    }
}