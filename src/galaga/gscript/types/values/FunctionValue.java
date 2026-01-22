package galaga.gscript.types.values;

import java.util.List;

import galaga.gscript.ast.statement.BlockStatement;
import galaga.gscript.interpreter.Scope;

public record FunctionValue(List<String> parameters, BlockStatement body, Scope parent) implements Value {
    
}
