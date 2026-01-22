package galaga.gscript.ast.expression.operator;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.expression.Expression;

public record BinaryExpression(Expression left, String operator, Expression right) implements Expression {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitBinaryExpression(this);
    }
}