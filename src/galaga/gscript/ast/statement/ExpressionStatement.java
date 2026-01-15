package galaga.gscript.ast.statement;

import galaga.gscript.ast.expression.ExpressionBase;

public record ExpressionStatement(ExpressionBase expression) implements StatementBase {

    @Override
    public String format() {
        return expression.format() + ";";
    }

}
