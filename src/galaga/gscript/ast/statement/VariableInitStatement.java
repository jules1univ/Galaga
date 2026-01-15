package galaga.gscript.ast.statement;

import galaga.gscript.ast.expression.ExpressionBase;
import galaga.gscript.ast.types.TypeBase;

public record VariableInitStatement(TypeBase name, ExpressionBase value) implements StatementBase {

    @Override
    public String format() {
        return name.format() + " = " + value.format() + ";";
    }

    
}
