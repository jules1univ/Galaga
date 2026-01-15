package galaga.gscript.ast.expression.literals;

import galaga.gscript.ast.expression.ExpressionBase;

public record LiteralBoolExpression(boolean value) implements ExpressionBase {

    @Override
    public String format() {
        return Boolean.toString(value);
    }

}
