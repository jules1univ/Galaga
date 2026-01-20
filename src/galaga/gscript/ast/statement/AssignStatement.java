package galaga.gscript.ast.statement;

import java.util.List;

import galaga.gscript.ast.expression.ExpressionBase;
import galaga.gscript.lexer.rules.Operator;

public record AssignStatement(String name, List<ExpressionBase> members, Operator op, ExpressionBase value)
        implements StatementBase {

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        for (ExpressionBase member : members) {
            sb.append(".").append(member.format());
        }
        sb.append(op.getText()).append(" ");
        sb.append(value.format());
        sb.append(";");
        return sb.toString();
    }

}
