package galaga.gscript.ast.declaration;

import java.util.List;

import galaga.gscript.ast.statement.Statement;
import galaga.gscript.ast.types.TypeFunction;

public record FunctionDeclaration(TypeFunction function, List<Statement> body) implements Declaration {

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append(function.format());
        sb.append(" {\n");
        for (Statement stmt : body) {
            sb.append("    ");
            sb.append(stmt.format());
            sb.append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
    
}
