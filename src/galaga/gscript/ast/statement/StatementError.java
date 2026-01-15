package galaga.gscript.ast.statement;

import galaga.gscript.ast.ASTNodeError;

public class StatementError extends ASTNodeError implements StatementBase {

    public StatementError(String message) {
        super(message);
    }

    @Override
    public String format() {
        return "/* StatementError: " + message + " */";
    }

}
