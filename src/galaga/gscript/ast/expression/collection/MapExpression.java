package galaga.gscript.ast.expression.collection;

import java.util.Map;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.expression.Expression;
import galaga.gscript.lexer.token.TokenRange;

public record MapExpression(Map<Expression, Expression> entries, TokenRange range) implements Expression {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitMapExpression(this);
    }

    @Override
    public TokenRange getRange() {
        return range;
    }
}