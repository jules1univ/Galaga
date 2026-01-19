package galaga.gscript.ast.expression;

import java.util.Map;

public record StructExpression(Map<String, ExpressionBase> fields) implements ExpressionBase {
    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        for (Map.Entry<String, ExpressionBase> entry : fields.entrySet()) {
            sb.append(entry.getKey());
            sb.append(" = ");
            sb.append(entry.getValue().format());
            if (entry != fields.entrySet().toArray()[fields.size() - 1]) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append(" }");
        return sb.toString();
    }
    
}
