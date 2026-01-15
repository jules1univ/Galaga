package galaga.gscript.ast.expression;

import java.util.List;

public record FunctionCallExpression(String name, List<ExpressionBase> args) implements ExpressionBase {

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("(");
        for (int i = 0; i < args.size(); i++) {
            sb.append(args.get(i).format());
            if (i < args.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
    
}
