package galaga.gscript.ast.statement;

import java.util.Optional;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.expression.Expression;

public record ReturnStatement(Optional<Expression> value) implements Statement {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitReturnStatement(this);
    }

}