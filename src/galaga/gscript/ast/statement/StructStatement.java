package galaga.gscript.ast.statement;

import java.util.Map;

import galaga.gscript.ast.expression.ExpressionBase;
import galaga.gscript.ast.types.TypeBase;

public record StructStatement(TypeBase name, Map<String, ExpressionBase> fields) implements StatementBase {
 
    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append(name.format());
        sb.append("{\n");
        for (var entry : fields.entrySet()) {
            sb.append(entry.getKey());
            sb.append(" = ");
            sb.append(entry.getValue().format());
            sb.append(",\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
