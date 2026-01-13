package galaga.gscript.parser.statement;

import galaga.gscript.parser.expression.Expression;

public class ReturnStatement extends Statement {

    private final Expression expression;

    public ReturnStatement(Expression expr) {
        super();
        this.expression = expr;
    }

    @Override
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("return ");
        sb.append(this.expression.format());
        sb.append(";\n");
        return sb.toString();
    }
    
}
