package galaga.gscript.ast.expression.literals;

import galaga.gscript.ast.expression.ExpressionBase;

public record LiteralFloatExpression(float value) implements ExpressionBase {

    @Override
    public String format() {
        return Float.toString(value);
    }

}
