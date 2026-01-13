package galaga.gscript.parser.statement;

import java.util.List;

import galaga.gscript.parser.expression.Expression;

public class WhileStatement extends Statement {
    private final boolean doWhile;
    private final Expression condition;
    private final List<Statement> body;

    public WhileStatement(Expression condition, List<Statement> body, boolean doWhile) {
        super();
        this.doWhile = doWhile;
        this.condition = condition;
        this.body = body;
    }

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        if (this.doWhile) {
            sb.append("do {\n");
        } else {
            sb.append("while (");
            sb.append(this.condition.format());
            sb.append(") {\n");
        }
        for (Statement stmt : body) {
            sb.append(stmt.format());
        }
        sb.append("}\n");
        if (this.doWhile) {
            sb.append("while (");
            sb.append(this.condition.format());
            sb.append(");\n");
        }
        return sb.toString();
    }
}
