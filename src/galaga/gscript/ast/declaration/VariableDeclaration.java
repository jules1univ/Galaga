package galaga.gscript.ast.declaration;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.expression.Expression;

public record VariableDeclaration(String name, Expression value, boolean isConstant) implements Declaration {
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitVariableDeclaration(this);
    }
}
