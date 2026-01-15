package galaga.gscript.ast.expression;

import galaga.gscript.ast.ASTNodeError;

public class ExpressionError extends ASTNodeError implements ExpressionBase {

    public ExpressionError(String message) {
        super(message);
    }

    @Override
    public String format() {
        return "/* ExpressionError: " + message + " */";
    }

}
