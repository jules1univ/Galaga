package galaga.gscript.ast.expression.function;

import java.util.List;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.expression.Expression;
import galaga.gscript.ast.statement.BlockStatement;
import galaga.gscript.lexer.token.Token;
import galaga.gscript.lexer.token.TokenRange;

public record FunctionExpression(List<Token> parameters, BlockStatement body, TokenRange range) implements Expression {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitFunctionExpression(this);
    }

    
    @Override
    public TokenRange getRange() {
        return range;
    }
}