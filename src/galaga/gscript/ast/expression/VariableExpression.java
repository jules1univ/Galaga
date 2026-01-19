package galaga.gscript.ast.expression;

import java.util.Optional;

public record VariableExpression(String name, Optional<ExpressionBase> invar) implements ExpressionBase {
    @Override
    public String format() {
        if (invar.isPresent()) {
            return name + "." + invar.get().format();
        } else {
            return name;
        }
    }
    
}
