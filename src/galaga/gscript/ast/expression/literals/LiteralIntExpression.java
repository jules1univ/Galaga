package galaga.gscript.ast.expression.literals;

import galaga.gscript.ast.expression.ExpressionBase;

public record LiteralIntExpression(int value) implements ExpressionBase {

    @Override
    public String format() {
        return Integer.toString(value);
    }
    
}
