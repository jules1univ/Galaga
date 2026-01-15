package galaga.gscript.ast.statement.logic;

import java.util.Map;
import java.util.Optional;

import galaga.gscript.ast.expression.ExpressionBase;
import galaga.gscript.ast.statement.StatementBase;

public record SwitchStatement(Map<ExpressionBase, StatementBase> cases, Optional<StatementBase> defaultCase) implements StatementBase {

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("switch {\n");
        for (var entry : cases.entrySet()) {
            sb.append("    case ");
            sb.append(entry.getKey().format());
            sb.append(" => ");
            sb.append(entry.getValue().format());
            sb.append("\n");
        }
        if (defaultCase.isPresent()) {
            sb.append("    default => ");
            sb.append(defaultCase.get().format());
            sb.append("\n");
        }
        sb.append("}");
        return sb.toString();
    }

}
