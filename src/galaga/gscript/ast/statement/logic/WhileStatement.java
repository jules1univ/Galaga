package galaga.gscript.ast.statement.logic;


import galaga.gscript.ast.expression.ExpressionBase;
import galaga.gscript.ast.statement.StatementBase;

public record WhileStatement(ExpressionBase condition, StatementBase body, boolean isDoWhile) implements StatementBase {

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        if (isDoWhile) {
            sb.append("do ");
            sb.append(body.format());
            sb.append("while (");
            sb.append(condition.format());
            sb.append(");");
        } else {
            sb.append("while (");
            sb.append(condition.format());
            sb.append(") ");
            sb.append(body.format());
        }
        return sb.toString();
    }

}
