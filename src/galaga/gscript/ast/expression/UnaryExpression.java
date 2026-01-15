package galaga.gscript.ast.expression;

import galaga.gscript.lexer.rules.Operator;

public record UnaryExpression(Operator operator, ExpressionBase operand) implements ExpressionBase {

    @Override
    public String format() {
        return operator.getText() + operand.format();
    }
    
}
