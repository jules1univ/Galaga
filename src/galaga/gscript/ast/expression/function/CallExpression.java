package galaga.gscript.ast.expression.function;

import java.util.List;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.expression.Expression;
import galaga.gscript.lexer.token.TokenRange;

public record CallExpression(Expression invoker, List<Expression> arguments, TokenRange range) implements Expression {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitCallExpression(this);
    }

    
    @Override
    public TokenRange getRange() {
        return range;
    }
}