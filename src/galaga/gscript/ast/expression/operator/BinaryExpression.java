package galaga.gscript.ast.expression.operator;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.expression.Expression;
import galaga.gscript.lexer.token.Token;
import galaga.gscript.lexer.token.TokenRange;

public record BinaryExpression(Expression left, Token operator, Expression right, TokenRange range) implements Expression {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitBinaryExpression(this);
    }

    
    @Override
    public TokenRange getRange() {
        return range;
    }
}