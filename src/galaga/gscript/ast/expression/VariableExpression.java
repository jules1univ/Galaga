package galaga.gscript.ast.expression;

import java.util.List;

public record VariableExpression(String name, List<ExpressionBase> members) implements ExpressionBase {
    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        for (var member : members) {
            sb.append(".").append(member.format());
        }
        return sb.toString();
    }
    
}
