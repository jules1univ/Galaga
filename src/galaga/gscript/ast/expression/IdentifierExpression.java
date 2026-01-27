package galaga.gscript.ast.expression;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.lexer.token.Token;
import galaga.gscript.lexer.token.TokenRange;

public record IdentifierExpression(Token name, TokenRange range) implements Expression {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitIdentifierExpression(this);
    }

    
    @Override
    public TokenRange getRange() {
        return range;
    }
}