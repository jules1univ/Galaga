package galaga.gscript.ast.expression;

import java.util.Optional;

public record VariableExpression(String name, Optional<ExpressionBase> accessField) implements ExpressionBase {
    @Override
    public String format() {
        if (accessField.isPresent()) {
            return name + "." + accessField.get().format();
        } else {
            return name;
        }
    }
    
}
