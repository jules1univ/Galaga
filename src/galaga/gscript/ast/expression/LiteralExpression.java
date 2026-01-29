package galaga.gscript.ast.expression;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.lexer.token.TokenRange;
import galaga.gscript.runtime.values.Value;

public record LiteralExpression(Value value, TokenRange range) implements Expression {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitLiteralExpression(this);
    }

    
    @Override
    public TokenRange getRange() {
        return range;
    }
}