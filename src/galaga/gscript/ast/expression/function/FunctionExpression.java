package galaga.gscript.ast.expression.function;

import java.util.List;

import galaga.gscript.ast.ASTVisitor;
import galaga.gscript.ast.expression.Expression;
import galaga.gscript.ast.statement.BlockStatement;

public record FunctionExpression(List<String> parameters, BlockStatement body) implements Expression {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitFunctionExpression(this);
    }
}