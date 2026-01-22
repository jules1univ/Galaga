package galaga.gscript.ast.expression.operator;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.expression.Expression;
import galaga.gscript.lexer.rules.Operator;

public record BinaryExpression(Expression left, Operator operator, Expression right) implements Expression {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitBinaryExpression(this);
    }
}