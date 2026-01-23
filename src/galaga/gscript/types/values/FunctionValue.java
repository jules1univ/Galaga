package galaga.gscript.types.values;

import java.util.List;
import java.util.Optional;

import galaga.gscript.ast.statement.BlockStatement;
import galaga.gscript.interpreter.Scope;

public record FunctionValue(Optional<String> name, List<String> parameters, BlockStatement body, Scope parent, boolean isNative) implements Value {

    @Override
    public <T> T getValue() {
        throw new UnsupportedOperationException("FunctionValue does not have a direct value representation.");
    }

    @Override
    public String toString() {
        if (name.isPresent()) {
            return "<Function " + name.get() + ">";
        } else {
            return "<Anonymous Function>";
        }
    }
    
}
