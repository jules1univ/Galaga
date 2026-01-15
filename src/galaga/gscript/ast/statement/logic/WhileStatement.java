package galaga.gscript.ast.statement.logic;


import galaga.gscript.ast.expression.Expression;
import galaga.gscript.ast.statement.Block;
import galaga.gscript.ast.statement.Statement;

public record WhileStatement(Expression condition, Block body, boolean isDoWhile) implements Statement {

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
