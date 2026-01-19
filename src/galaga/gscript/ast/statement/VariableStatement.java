package galaga.gscript.ast.statement;

import java.util.Optional;

import galaga.gscript.ast.expression.ExpressionBase;
import galaga.gscript.ast.types.TypeBase;

public record VariableStatement(TypeBase name, Optional<VariableStatement> invar, Optional<ExpressionBase> value) implements StatementBase {

    @Override
    public String format() {
        if(invar.isPresent())
        {
            return name.format() + "." + invar.get().format();
        }else{
            return name.format() + " = " + value.get().format() + ";";
        }
    }

    
}
