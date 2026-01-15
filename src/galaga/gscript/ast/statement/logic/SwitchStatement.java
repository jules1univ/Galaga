package galaga.gscript.ast.statement.logic;

import java.util.Map;

import galaga.gscript.ast.expression.Expression;
import galaga.gscript.ast.statement.Block;
import galaga.gscript.ast.statement.Statement;

public record SwitchStatement(Map<Expression, Block> cases) implements Statement {

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
        sb.append("}");
        return sb.toString();
    }

}
