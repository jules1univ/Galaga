package galaga.gscript.ast.expression;

import galaga.gscript.ast.ASTVisitor;

public record IdentifierExpression(String name) implements Expression {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitIdentifierExpression(this);
    }
}