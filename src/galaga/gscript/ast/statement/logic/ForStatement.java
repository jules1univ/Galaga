package galaga.gscript.ast.statement.logic;

import galaga.gscript.ast.expression.ExpressionBase;
import galaga.gscript.ast.statement.Block;
import galaga.gscript.ast.statement.StatementBase;
import galaga.gscript.ast.statement.VariableStatement;

public record ForStatement(VariableStatement init, ExpressionBase check, ExpressionBase action, Block body) implements StatementBase {

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("for (");
        sb.append(init.format()).append(" ");
        sb.append(check.format()).append("; ");
        sb.append(action.format());
        sb.append(") ");
        sb.append(body.format());
        return sb.toString();
    }
    
}
