package galaga.gscript.ast.statement.logic;

import java.util.Map;
import java.util.Optional;

import galaga.gscript.ast.expression.ExpressionBase;
import galaga.gscript.ast.statement.StatementBase;

public record IfStatement(Map<ExpressionBase, StatementBase> conditions, Optional<StatementBase> elseCondition) implements StatementBase {

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (var entry : conditions.entrySet()) {
            if (first) {
                sb.append("if (");
                first = false;
            } else {
                sb.append(" else if (");
            }
            sb.append(entry.getKey().format());
            sb.append(") ");
            sb.append(entry.getValue().format());
        }

        if (elseCondition.isPresent()) {
            sb.append(" else ");
            sb.append(elseCondition.get().format());
        }

        return sb.toString();
    }

}
