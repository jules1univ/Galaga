package galaga.gscript.ast.statement.logic;

import java.util.Map;
import java.util.Optional;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.expression.Expression;
import galaga.gscript.ast.statement.BlockStatement;
import galaga.gscript.ast.statement.Statement;
import galaga.gscript.lexer.token.TokenRange;

public record IfStatement(Map<Expression, BlockStatement> ifElseBranch, Optional<BlockStatement> elseBranch, TokenRange range) implements Statement {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitIfStatement(this);
    }

    
    @Override
    public TokenRange getRange() {
        return range;
    }
}