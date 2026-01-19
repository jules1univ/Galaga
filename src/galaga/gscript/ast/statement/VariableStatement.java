package galaga.gscript.ast.statement;

import galaga.gscript.ast.expression.ExpressionBase;
import galaga.gscript.ast.types.Type;

public record VariableStatement(Type type, String name,ExpressionBase value) implements StatementBase {

    @Override
    public String format() {
        return type.format() + " " + name + " = " + value.format() + ";";
    }

    
}
