package galaga.gscript.ast.expression.literals;

import galaga.gscript.ast.expression.ExpressionBase;

public record LiteralStringExpression(String value) implements ExpressionBase {

    @Override
    public String format() {
        String escapeValue = value.replace("\n","\\n").replace("\"","\\\"").replace("\t","\\t").replace("\r","\\r");
        return "\"" + escapeValue + "\"";
    }
    
}
