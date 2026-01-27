package galaga.gscript.ast.statement;

import java.util.Optional;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.expression.Expression;
import galaga.gscript.lexer.token.TokenRange;

public record ReturnStatement(Optional<Expression> value, TokenRange range) implements Statement {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitReturnStatement(this);
    }

    
    @Override
    public TokenRange getRange() {
        return range;
    }

}