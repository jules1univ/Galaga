package galaga.gscript.ast.statement;

import galaga.gscript.ast.expression.ExpressionBase;
import galaga.gscript.ast.types.Type;

public record VariableStatement(Type type, String name, ExpressionBase value) implements StatementBase {

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append(type.format()).append(" ");
        sb.append(name).append(" ");
        sb.append("= ");
        sb.append(value.format());
        sb.append(";");
        return sb.toString();
    }

}
