package galaga.gscript.ast.declaration;

import galaga.gscript.ast.statement.StatementBase;
import galaga.gscript.ast.types.TypeBase;

public record FunctionDeclaration(TypeBase function, StatementBase body) implements DeclarationBase {

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append(function.format());
        sb.append(" ");
        sb.append(body.format());
        return sb.toString();
    }
    
}
