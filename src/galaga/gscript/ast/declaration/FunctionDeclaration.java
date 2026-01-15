package galaga.gscript.ast.declaration;

import galaga.gscript.ast.statement.Block;
import galaga.gscript.ast.types.TypeFunction;

public record FunctionDeclaration(TypeFunction function, Block body) implements Declaration {

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append(function.format());
        sb.append(" ");
        sb.append(body.format());
        return sb.toString();
    }
    
}
