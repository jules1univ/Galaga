package galaga.gscript.ast.statement.logic.loop;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.expression.Expression;
import galaga.gscript.ast.statement.BlockStatement;
import galaga.gscript.ast.statement.Statement;
import galaga.gscript.lexer.token.Token;
import galaga.gscript.lexer.token.TokenRange;

public record ForStatement(Token variable, Expression iterable, BlockStatement body, TokenRange range) implements Statement {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitForStatement(this);
    }

    
    @Override
    public TokenRange getRange() {
        return range;
    }
}