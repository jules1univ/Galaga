package galaga.gscript.ast.expression;

public record VariableExpression(String name) implements ExpressionBase {
    @Override
    public String format() {
        return name;
    }
    
}
