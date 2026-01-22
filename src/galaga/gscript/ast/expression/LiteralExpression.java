package galaga.gscript.ast.expression;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.types.values.Value;

public record LiteralExpression(Value value) implements Expression {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitLiteralExpression(this);
    }
}