package galaga.gscript.parser.statement;

import java.util.List;
import java.util.Map;

import galaga.gscript.parser.expression.Expression;

public class IfStatement extends Statement {

    private final Map<Expression, List<Statement>> branches;

    public IfStatement(Map<Expression, List<Statement>> branches) {
        super();
        this.branches = branches;
    }

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<Expression, List<Statement>> entry : branches.entrySet()) {
            Expression condition = entry.getKey();
            List<Statement> body = entry.getValue();
            if (first) {
                sb.append("if (");
                first = false;
            } else {
                sb.append("else if (");
            }
            sb.append(condition.format());
            sb.append(") {\n");
            for (Statement stmt : body) {
                sb.append(stmt.format());
            }
            sb.append("}\n");
        }
        return sb.toString();
    }
}
