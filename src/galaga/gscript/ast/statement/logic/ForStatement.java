package galaga.gscript.ast.statement.logic;

import java.util.List;

import galaga.gscript.ast.expression.ExpressionBase;
import galaga.gscript.ast.statement.Block;
import galaga.gscript.ast.statement.StatementBase;

public record ForStatement(List<ExpressionBase> conditions, Block body) implements StatementBase {

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("for (");
        for (int i = 0; i < conditions.size(); i++) {
            sb.append(conditions.get(i).format());
            if (i < conditions.size() - 1) {
                sb.append("; ");
            }
        }
        sb.append(") ");
        sb.append(body.format());
        return sb.toString();
    }
    
}
