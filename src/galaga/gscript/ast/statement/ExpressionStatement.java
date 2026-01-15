package galaga.gscript.ast.statement;

import galaga.gscript.ast.expression.Expression;

public record ExpressionStatement(Expression expression) implements Statement {

    @Override
    public String format() {
        return expression.format() + ";";
    }

}
