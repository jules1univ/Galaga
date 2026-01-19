package galaga.gscript.ast.statement;

import java.util.Map;

import galaga.gscript.ast.expression.ExpressionBase;
import galaga.gscript.ast.types.Type;

public record StructStatement(Type type, String name, Map<String, ExpressionBase> fields) implements StatementBase {

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append(type.format());
        sb.append(" ");
        sb.append(name);
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
