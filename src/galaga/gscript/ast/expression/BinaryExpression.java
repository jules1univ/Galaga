package galaga.gscript.ast.expression;

import galaga.gscript.lexer.rules.Operator;

public record BinaryExpression(ExpressionBase left, Operator operator, ExpressionBase right) implements ExpressionBase {

    @Override
    public String format() {
        return left.format() + " " + operator.getText() + " " + right.format();
    }
}
