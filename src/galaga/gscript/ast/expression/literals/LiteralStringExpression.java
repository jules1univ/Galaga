package galaga.gscript.ast.expression.literals;

import galaga.gscript.ast.expression.ExpressionBase;

public record LiteralStringExpression(String value) implements ExpressionBase {

    @Override
    public String format() {
        return "\"" + value + "\"";
    }
    
}
