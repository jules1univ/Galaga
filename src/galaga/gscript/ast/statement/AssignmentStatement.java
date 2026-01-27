package galaga.gscript.ast.statement;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.expression.Expression;
import galaga.gscript.lexer.token.Token;
import galaga.gscript.lexer.token.TokenRange;

public record AssignmentStatement(Token name, Token operator, Expression value, boolean isConstant, TokenRange range) implements Statement {
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitAssignmentStatement(this);
    }
    
    
    @Override
    public TokenRange getRange() {
        return range;
    }
}
