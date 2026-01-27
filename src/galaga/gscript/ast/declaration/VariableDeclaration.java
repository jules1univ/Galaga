package galaga.gscript.ast.declaration;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.expression.Expression;
import galaga.gscript.lexer.token.Token;
import galaga.gscript.lexer.token.TokenRange;

public record VariableDeclaration(Token name, Expression value, boolean isConstant, TokenRange range) implements Declaration {
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitVariableDeclaration(this);
    }

    
    @Override
    public TokenRange getRange() {
        return range;
    }
}
